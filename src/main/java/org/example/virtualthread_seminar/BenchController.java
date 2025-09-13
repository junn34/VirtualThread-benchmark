package org.example.virtualthread_seminar;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class BenchController {

    private final BenchService svc;

    public BenchController(BenchService svc) {
        this.svc = svc;
    }

    // 1) CPU-bound: /cpu?ms=50
    @GetMapping("cpu")
    public String cpu(@RequestParam(name = "ms", defaultValue = "50") long ms) {
        svc.burnCpu(ms);
        return "cpu " + ms + "ms";
    }

    // 2) DB I/O-bound: /db/item?id=123&delayMs=0
    @GetMapping("db/item")
    public Map<String, Object> db(@RequestParam(name = "id") long id,
                                  @RequestParam(name = "delayMs", defaultValue = "0") int delayMs) {
        return svc.getItem(id, delayMs);
    }

    // 3) Redis 캐시: /cache/item?id=123

    @Cacheable(cacheNames = "itemById", key = "#a0")
    @GetMapping("/cache/item")
    public Map<String, Object> cached(@RequestParam(name = "id") long id) {
        return svc.getItem(id, 0);
    }




    // 4) 혼합 시나리오: /mix?id=123&cpuMs=20&repeat=3
    @GetMapping("mix")
    public List<Map<String, Object>> mix(@RequestParam(name = "id") long id,
                                         @RequestParam(name = "cpuMs", defaultValue = "20") long cpuMs,
                                         @RequestParam(name = "repeat", defaultValue = "3") int repeat) {
        if (repeat < 1) repeat = 1;
        if (repeat > 1000) repeat = 1000;

        List<Map<String, Object>> out = new ArrayList<>(repeat);
        for (int i = 0; i < repeat; i++) {
            svc.burnCpu(cpuMs);          // CPU 부하
            out.add(svc.getItem(id, 0)); // DB 조회
        }
        return out;
    }

    // (옵션) VT 동작 여부 확인: /vt
    @GetMapping("vt")
    public Map<String, Object> vt() {
        Thread t = Thread.currentThread();
        return Map.of("name", t.getName(), "isVirtual", t.isVirtual());
    }
}
