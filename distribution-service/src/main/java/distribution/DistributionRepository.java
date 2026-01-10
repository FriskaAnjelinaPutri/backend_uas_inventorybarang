package distribution;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DistributionRepository
        extends MongoRepository<Distribution, String> {
}
