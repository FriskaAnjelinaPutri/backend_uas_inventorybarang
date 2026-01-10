package distribution;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DistributionService {

    private final DistributionRepository repository;

    public DistributionService(DistributionRepository repository) {
        this.repository = repository;
    }

    public Distribution create(Distribution d) {
        // Auto-generate ID: find max ID and increment (as string "1", "2", "3", etc)
        if (d.getId() == null || d.getId().isEmpty()) {
            String nextId = getNextId();
            d.setId(nextId);
        }
        return repository.save(d);
    }

    private String getNextId() {
        // Find the document with the highest numeric ID
        List<Distribution> all = repository.findAll();
        if (all.isEmpty()) {
            return "1"; // Start from 1 if no data exists
        }
        
        long maxId = all.stream()
                .map(Distribution::getId)
                .filter(id -> id != null && !id.isEmpty())
                .filter(id -> {
                    try {
                        Long.parseLong(id);
                        return true;
                    } catch (NumberFormatException e) {
                        return false; // Skip non-numeric IDs (old ObjectIds)
                    }
                })
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L);
        
        return String.valueOf(maxId + 1);
    }

    public List<Distribution> findAll() {
        return repository.findAll();
    }

    public Distribution findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Distribution with id " + id + " not found"));
    }

    public Distribution update(String id, Distribution data) {
        Distribution d = findById(id);
        if (data.getIdProduk() != null) {
            d.setIdProduk(data.getIdProduk());
        }
        if (data.getJumlahKirim() != null) {
            d.setJumlahKirim(data.getJumlahKirim());
        }
        if (data.getTujuan() != null) {
            d.setTujuan(data.getTujuan());
        }
        if (data.getStatusPengiriman() != null) {
            d.setStatusPengiriman(data.getStatusPengiriman());
        }
        return repository.save(d);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Distribution with id " + id + " not found");
        }
        repository.deleteById(id);
    }
}
