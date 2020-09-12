/**
 * Provides the interfaces to represent GPS data.
 *
 * <p>A typical example would be a point of interest (also known as a {@code Waypoint}), with its
 * latitude, its longitude and its elevation. Another typical example include a {@code Trail} (i.e.
 * a collection of {@code Waypoint}s logged by a GPS device).
 *
 * <p>Immutable instances can be instantiated using the appropriate builders. All builders follow
 * these 3 principles:
 *
 * <ol>
 *   <li>Mandatory fields must passed to the builder's constructor.
 *   <li>Optional fields can be passed using the appropriate setters.
 *   <li>Each builder offers a static method of(), which can be used to quickly create a new
 *       instance out of an existing one.
 * </ol>
 *
 * <p>For example:
 *
 * <ol>
 *   <li>To instantiate a new immutable {@code Waypoint} with just the mandatory fields, use the
 *       {@code WaypointBuilder} as follows:<br>
 *       <code>
 * Waypoint pt = new WaypointBuilder(time, coordinates).build();
 * </code>
 *   <li>To instantiate a new immutable {@code Waypoint} with some optional field, use the
 *       appropriate setter:<br>
 *       <code>
 * Waypoint pt = new WaypointBuilder(time, coordinates).name(name).build();
 * </code>
 *   <li>To "update" an existing instance, use the static {@code of} method:<br>
 *       <code>
 * Waypoint npt = WaypointBuilder.of(pt).time(newTime).build();
 * </code>
 * </ol>
 *
 * @author Xavier Sosnovsky
 */
package ws.sosna.pinetrail.model;
