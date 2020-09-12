/**
 * Provides thread-safe readers that extract information from GPX data files.
 *
 * <p>{@code Gpx11Provider} and {@code Gpx10Provider} act as service providers, as defined in Java
 * SPI, and will be automatically registered with the service-provider loading facility offered by
 * the {@code Readers} class.
 *
 * @author Xavier Sosnovsky
 * @see ws.sosna.pinetrail.api.io.Readers
 * @see java.util.ServiceLoader
 */
package ws.sosna.pinetrail.gpx;
