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

int main() {
    // 1. CPU Affinity: Lock to Core 0 to minimize context switching
    cpu_set_t mask;
    CPU_ZERO(&mask);
    CPU_SET(0, &mask);
    sched_setaffinity(0, sizeof(mask), &mask);

    struct timespec t_start, t_pre_conn, t_post_conn, t_pre_send, t_post_send;
    clock_gettime(CLOCK_MONOTONIC, &t_start);

    // 2. Prepare Source File for sendfile
    int fd = open("data.bin", O_RDONLY);
    int sock = socket(AF_INET, SOCK_STREAM, 0);

    // 3. TCP_NODELAY: Disable Nagle's Algorithm
    int opt = 1;
    setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, &opt, sizeof(opt));

    struct sockaddr_in addr = { .sin_family = AF_INET, .sin_port = htons(8080) };
    inet_pton(AF_INET, "127.0.0.1", &addr.sin_addr);

    clock_gettime(CLOCK_MONOTONIC, &t_pre_conn);
    connect(sock, (struct sockaddr *)&addr, sizeof(addr));
    clock_gettime(CLOCK_MONOTONIC, &t_post_conn);

    // 4. Performance Critical Send
    off_t offset = 0;
    clock_gettime(CLOCK_MONOTONIC, &t_pre_send);
    
    sendfile(sock, fd, &offset, SIZE);
    
    clock_gettime(CLOCK_MONOTONIC, &t_post_send);

    // Output Latencies
    printf("Connection Latency: %ld ns\n", (t_post_conn.tv_nsec - t_pre_conn.tv_nsec));
    printf("Sendfile Latency:   %ld ns\n", (t_post_send.tv_nsec - t_pre_send.tv_nsec));

    close(sock); close(fd);
    return 0;
}
