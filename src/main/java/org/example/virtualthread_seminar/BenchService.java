package org.example.virtualthread_seminar;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BenchService {
    private final ItemRepository repo;

    public BenchService(ItemRepository repo) {
        this.repo = repo;
    }

    // DB I/O (옵션으로 지연)
    public Map<String, Object> getItem(long id, int delayMs) {
        if (delayMs > 0) {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
        }
        return repo.findById(id);
    }

    // CPU-bound
    public void burnCpu(long ms) {
        long end = System.nanoTime() + ms * 1_000_000;
        long x = 0;
        while (System.nanoTime() < end) { x++; }
    }
}
