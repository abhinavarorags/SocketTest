#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/sendfile.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sched.h>
#include <time.h>

#define SIZE 1024

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <IPv6_Address>\n", argv[0]);
        return 1;
    }

    // 1. Pin to Core 0 for stability
    cpu_set_t mask;
    CPU_ZERO(&mask);
    CPU_SET(0, &mask);
    sched_setaffinity(0, sizeof(mask), &mask);

    struct timespec t_start, t_pre_conn, t_post_conn, t_pre_send, t_post_send;
    clock_gettime(CLOCK_MONOTONIC, &t_start);

    // 2. Setup Sockets (AF_INET6 for Google's IPv6)
    int fd = open("data.bin", O_RDONLY);
    if (fd < 0) { perror("File open failed"); return 1; }

    int sock = socket(AF_INET6, SOCK_STREAM, 0);
    
    int opt = 1;
    setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, &opt, sizeof(opt));

    struct sockaddr_in6 addr = {0};
    addr.sin6_family = AF_INET6;
    addr.sin6_port = htons(80); // Using Port 80 for a standard web test
    
    if (inet_pton(AF_INET6, argv[1], &addr.sin6_addr) <= 0) {
        fprintf(stderr, "Invalid IPv6 address format\n");
        return 1;
    }

    // 3. Connection Measurement
    clock_gettime(CLOCK_MONOTONIC, &t_pre_conn);
    if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        perror("Connect failed");
        return 1;
    }
    clock_gettime(CLOCK_MONOTONIC, &t_post_conn);

    // 4. Sendfile Measurement
    off_t offset = 0;
    clock_gettime(CLOCK_MONOTONIC, &t_pre_send);
    sendfile(sock, fd, &offset, SIZE);
    clock_gettime(CLOCK_MONOTONIC, &t_post_send);

    // Results
    long conn_lat = (t_post_conn.tv_sec - t_pre_conn.tv_sec) * 1e9 + (t_post_conn.tv_nsec - t_pre_conn.tv_nsec);
    long send_lat = (t_post_send.tv_sec - t_pre_send.tv_sec) * 1e9 + (t_post_send.tv_nsec - t_pre_send.tv_nsec);

    printf("Connected to: %s\n", argv[1]);
    printf("Connection Latency: %ld ns\n", conn_lat);
    printf("Sendfile Latency:   %ld ns\n", send_lat);

    close(sock); close(fd);
    return 0;
}
