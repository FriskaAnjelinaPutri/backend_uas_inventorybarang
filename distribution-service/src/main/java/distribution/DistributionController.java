package distribution;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/distributions")
public class DistributionController {

    private final DistributionService service;

    public DistributionController(DistributionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Distribution> create(@RequestBody Distribution d) {
        Distribution created = service.create(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Distribution>> getAll() {
        List<Distribution> distributions = service.findAll();
        return ResponseEntity.ok(distributions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Distribution> getById(@PathVariable String id) {
        Distribution distribution = service.findById(id);
        return ResponseEntity.ok(distribution);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Distribution> update(@PathVariable String id,
                                               @RequestBody Distribution d) {
        Distribution updated = service.update(id, d);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        service.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Distribution with id " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
