package distribution;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/distributions")
@CrossOrigin(origins = "*")
public class DistributionController {

    private final DistributionService service;

    public DistributionController(DistributionService service) {
        this.service = service;
    }

    // ========================
    // CREATE
    // ========================
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Distribution distribution) {
        Distribution created = service.create(distribution);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Distribution berhasil ditambahkan");
        response.put("data", created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ========================
    // READ ALL
    // ========================
    @GetMapping
    public ResponseEntity<List<Distribution>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // ========================
    // READ BY ID
    // ========================
    @GetMapping("/{id}")
    public ResponseEntity<Distribution> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // ========================
    // UPDATE
    // ========================
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable String id,
            @RequestBody Distribution distribution
    ) {
        Distribution updated = service.update(id, distribution);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Distribution berhasil diperbarui");
        response.put("data", updated);

        return ResponseEntity.ok(response);
    }

    // ========================
    // DELETE
    // ========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        service.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Distribution dengan id " + id + " berhasil dihapus");

        return ResponseEntity.ok(response);
    }

    // ========================
    // EXCEPTION HANDLER
    // ========================
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
