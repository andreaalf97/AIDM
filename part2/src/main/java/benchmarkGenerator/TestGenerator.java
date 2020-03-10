package benchmarkGenerator;

/**
 * This interfaces represents the generator for a single instance or for an entire benchmark
 */
public interface TestGenerator {

    TestInstance getRandomTest();

}
