package ch.supsi.dti.isin;

/**
 * All interfaces that extends {@link Contract} are intended
 * to test interface contracts.
 *
 * <p>
 * For example if we have an interface {@code I}, we can write
 * a contract for such interface and when a class {@code C}
 * implements the interface {@code I} the test class for
 * {@code C} should implement the contract to test that
 * {@code C} implements {@code I} in the expected way.
 *
 * @param <T> the type to test.
 * @author Massimo Coluzzi
 */
public interface Contract<T>
{

    /**
     * An instance of the class that should respect the contract.
     *
     * @return instance of the class to test.
     */
    T sampleValue();

}
