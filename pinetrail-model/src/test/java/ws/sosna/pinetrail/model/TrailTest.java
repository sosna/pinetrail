/*
 * Copyright (c) 2014, Xavier Sosnovsky <xso@sosna.ws>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */
package ws.sosna.pinetrail.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * @author Xavier Sosnovsky
 */
public class TrailTest extends DescribableTest {

    private Trail trail;

    @Before
    public void setup() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), "", "tmp",
                new LinkedHashSet<>());
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        trail = newTrail(points, "tmp", "tmp", new LinkedHashSet<>(), null, null);
    }

    @Override
    Describable createDescribable(final String name,
            final String description, final Set<Link> links) {
        return TrailBuilder.of(trail).name(name).description(description).links(
            links).build();
    }

    @Test
    public void createInstance() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail
                = newTrail(points, "trail", null, null, null, null);
        assertNotNull(trail);
    }

    @Test
    public void getPoints() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail
                = newTrail(points, "trail", null, null, null, null);
        assertEquals(points, trail.getWaypoints());
    }

    @Test
    public void getLevel() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail
                = newTrail(points, "trail", null, null, 4, null);
        assertTrue(4 == trail.getDifficultyRating());
    }

    @Test
    public void getActivity() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail
                = newTrail(points, "trail", null, null, null,
                        Activity.HIKING);
        assertEquals(Activity.HIKING, trail.getActivity());
    }

    @Test
    public void equalityPoints() {
        final Waypoint point1 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points1 = new LinkedHashSet<>();
        points1.add(point1);
        final Waypoint point2 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 127.9), null, null, null);
        final Set<Waypoint> points2 = new LinkedHashSet<>();
        points2.add(point2);
        final Trail trail1 = newTrail(points1, "trail", null, null,
                null, null);
        final Trail trail2 = newTrail(points2, "trail", null, null,
                null, null);
        assertFalse(trail1.equals(trail2));
    }

    @Test
    public void equalityLevel() {
        final Waypoint point1 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points1 = new LinkedHashSet<>();
        points1.add(point1);
        final Trail trail1 = newTrail(points1, "trail", null, null,
                null, null);
        final Trail trail2 = newTrail(points1, "trail", null, null, 6, null);
        assertFalse(trail1.equals(trail2));
    }

    @Test
    public void equalityActivity() {
        final Waypoint point1 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points1 = new LinkedHashSet<>();
        points1.add(point1);
        final Trail trail1 = newTrail(points1, "trail", null, null,
                null, Activity.JOGGING);
        final Trail trail2 = newTrail(points1, "trail", null, null,
                null, null);
        assertFalse(trail1.equals(trail2));
    }

    @Test
    @Override
    public void hashCodeNOK() throws URISyntaxException {
        super.hashCodeNOK();
        final Waypoint point1 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points1 = new LinkedHashSet<>();
        points1.add(point1);
        final Waypoint point2 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 127.9), null, null, null);
        final Set<Waypoint> points2 = new LinkedHashSet<>();
        points2.add(point2);
        final Trail trail1 = newTrail(points1, "trail", null, null,
                null, null);
        final Trail trail2 = newTrail(points2, "trail", null, null,
                null, null);
        assertFalse(trail1.hashCode() == trail2.hashCode());
    }

    @Test
    @Override
    public void hashCodeOK() {
        final Trail trail1 = TrailBuilder.of(trail).build();
        final Trail trail2 = TrailBuilder.of(trail).build();
        assertEquals(trail1.hashCode(), trail2.hashCode());
    }

    @Test
    public void toStringOutput() {
        final Coordinates coordinates = newCoordinates(8.789654, 40.6784356, 393.31);
        final Waypoint point = newWaypoint(Instant.EPOCH, coordinates, null,
                null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail = newTrail(points, "trail", null, null, null,
                null, countries);
        System.out.println(trail);
        assertEquals(
                "Trail{id=" + trail.getId() + ", points=[Waypoint{time=" + Instant.EPOCH
                + ", coordinates=" + coordinates.toString()
                + ", name=null, description=null, links=[], "
                + "type=null, distance=0.0, elevationDifference=0.0, "
                + "grade=0.0, speed=0.0, isActive=false, timeDifference=0}], "
                + "name=trail, description=null, links=[], "
                + "difficulty rating=0, activity=BIKING, countries=[DE],"
                + " rating=0, statistics=" + trail.getStatistics() + '}',
                trail.toString());
    }

    @Test(expected = ValidationException.class)
    public void valPointsNull() {
        newTrail(null, "trail", null, null, null, null);
    }

    @Test(expected = ValidationException.class)
    public void valPointsEmpty() {
        newTrail(new LinkedHashSet<>(), "trail", null, null, null, null);
    }

    @Test(expected = ValidationException.class)
    public void valNameNull() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        newTrail(points, null, null, null, null, null);
    }

    @Test(expected = ValidationException.class)
    public void valNameEmpty() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        newTrail(points, " ", null, null, null, null);
    }

    @Test
    public void pointsImmutable() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail = newTrail(points, "trail", null, null, null,
                null);
        final Waypoint point2 = newWaypoint(Instant.parse("2014-12-23T14:56:32Z"),
                newCoordinates(8.789654, 40.6784356, 137.8), null, null, null);
        points.add(point2);
        assertEquals(1, trail.getWaypoints().size());
        assertFalse(trail.getWaypoints().contains(point2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pointsImmutableFromGetter() {
        final Waypoint point = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail = newTrail(points, "trail", null, null, null,
                null);
        trail.getWaypoints().clear();
    }

    @Test
    public void pointsSorted() {
        final Waypoint point1 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Waypoint point2 = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        final Trail trail = newTrail(points, "trail", null, null, null,
                null);
        assertEquals(2, trail.getWaypoints().size());
        int count = 0;
        for (final Waypoint point : trail.getWaypoints()) {
            assertTrue(
                    point.getTime() == (0 == count ? Instant.MIN : Instant.EPOCH));
            count++;
        }
    }

    @Test
    public void of() throws URISyntaxException {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        final Set<Link> links = new LinkedHashSet<>();
        links.add(new LinkBuilder("wiki", new URI("http://www.wiki.org")).
                build());
        countries.add("DE");
        final Trail original = new TrailBuilder("trail", points).description(
                "desc").links(links).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        final Trail copy = TrailBuilder.of(original).build();
        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    public void updatePoints() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail original = newTrail(points, "trail", "desc", null,
                4, Activity.HIKING);
        final Waypoint point2 = newWaypoint(Instant.EPOCH,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        points.add(point2);
        final Trail copy = TrailBuilder.of(original).points(points).build();
        assertNotSame(original, copy);
        assertFalse(original.equals(copy));
        assertEquals(points, copy.getWaypoints());
    }

    @Test
    public void updateName() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail original = newTrail(points, "trail", "desc", null,
                4, Activity.HIKING);
        final String newName = "trail2";
        final Trail copy = TrailBuilder.of(original).name(newName).build();
        assertNotSame(original, copy);
        assertFalse(original.equals(copy));
        assertEquals(newName, copy.getName());
    }

    @Test
    public void getCountries() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        assertEquals(countries, trail.getCountries());
    }

    @Test
    public void countriesImmutable() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        countries.add("BE");
        assertEquals(1, trail.getCountries().size());
        assertTrue(trail.getCountries().contains("DE"));
    }

    @Test
    public void tryGuessingCountryIfEmpty() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        //1 if country could be guessed, 0 otherwise
        assertTrue(1 == trail.getCountries().size() ||
            0 == trail.getCountries().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void countriesImmutableFromGetter() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        trail.getCountries().clear();
    }

    @Test
    public void updateCountries() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail original = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).build();
        final Trail copy = TrailBuilder.of(original).countries(countries).
                build();
        assertFalse(original.equals(copy));
        assertEquals(countries, copy.getCountries());
    }

    @Test
    public void countriesAffectHashcode() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail original = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).build();
        final Trail copy = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        assertFalse(original.hashCode() == copy.hashCode());
    }

    @Test
    public void countriesAffectEquality() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail original = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).build();
        final Trail copy = new TrailBuilder("trail", points).description(
                "desc").links(null).difficultyRating(4).activity(
                        Activity.HIKING).countries(countries).build();
        assertFalse(original.equals(copy));
    }

    @Test
    public void idNotNull() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail = new TrailBuilder("trail", points).build();
        assertNotNull(trail.getId());
    }

    @Test
    public void idNotAffectByUpdate() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail1 = new TrailBuilder("trail", points).build();
        assertNotNull(trail1.getId());
        final Trail trail2 = TrailBuilder.of(trail1).name("trail2").build();
        assertNotNull(trail2.getId());
        assertEquals(trail1.getId(), trail2.getId());
    }

    @Test
    public void idAffectsEquals() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail1 = new TrailBuilder("trail", points).build();
        final Trail trail2 = new TrailBuilder("trail", points).build();
        final Trail trail3 = TrailBuilder.of(trail1).build();
        assertEquals(trail1, trail3);
        assertFalse(trail1.equals(trail2));
    }

    @Test
    public void idAffectsHashcode() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail1 = new TrailBuilder("trail", points).build();
        final Trail trail2 = new TrailBuilder("trail", points).build();
        final Trail trail3 = TrailBuilder.of(trail1).build();
        assertEquals(trail1.hashCode(), trail3.hashCode());
        assertFalse(trail1.hashCode() == trail2.hashCode());
    }

    @Test
    public void getRating() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").rating(3).build();
        assertEquals(3, trail.getRating());
    }

    @Test(expected = ValidationException.class)
    public void valRatingMin() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        new TrailBuilder("trail", points).
                rating(-1).countries(countries).build();
    }

    @Test(expected = ValidationException.class)
    public void valRatingMax() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        new TrailBuilder("trail", points).rating(6).
                countries(countries).build();
    }

    @Test
    public void ratingDefault() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").build();
        assertEquals(0, trail.getRating());
    }

    @Test
    public void ratingAffectsHashcode() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail1 = new TrailBuilder("trail", points).description(
                "desc").build();
        final Trail trail2 = TrailBuilder.of(trail1).rating(5).build();
        assertFalse(trail1.equals(trail2));
    }

    @Test
    public void ratingAffectsEqual() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail1 = new TrailBuilder("trail", points).description(
                "desc").build();
        final Trail trail2 = TrailBuilder.of(trail1).rating(5).build();
        assertFalse(trail1.hashCode() == trail2.hashCode());
    }

    @Test
    public void statsComputed() {
        final Waypoint point = newWaypoint(Instant.MIN,
                newCoordinates(8.789654, 40.6784356, 393.31), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point);
        final Trail trail = new TrailBuilder("trail", points).description(
                "desc").build();
        assertNotNull(trail.getStatistics());
    }

    @Test
    public void keepActivity() {
        final Waypoint point1 = newWaypoint(Instant.parse("2014-05-18T08:27:02Z"),
                newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null, null);
        final Waypoint point2 = newWaypoint(Instant.parse("2014-05-18T08:27:09Z"),
                newCoordinates(7.9631571192, 50.1184399333, 215.47), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        final Trail trail = newTrail(points, "trail", null, null, null,
                Activity.JOGGING);
        assertEquals(Activity.JOGGING, trail.getActivity());
    }

    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        final Waypoint point1 = newWaypoint(Instant.parse("2014-05-18T08:27:02Z"),
                newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null, null);
        final Waypoint point2 = newWaypoint(Instant.parse("2014-05-18T08:27:09Z"),
                newCoordinates(7.9631571192, 50.1184399333, 215.47), null, null, null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        final Trail trail = newTrail(points, "trail", "trail desc", null, null,
                Activity.JOGGING);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(trail);
        oos.close();

        final byte[] recovered = out.toByteArray();
        final InputStream in = new ByteArrayInputStream(recovered);
        final ObjectInputStream ois = new ObjectInputStream(in);
        final Trail recoveredTrail = (Trail) ois.readObject();

        assertEquals(trail, recoveredTrail);
    }

    private Coordinates newCoordinates(final Double longitude,
            final Double latitude, final Double elevation) {
        return new CoordinatesBuilder(longitude, latitude).elevation(elevation).
                build();
    }

    private Waypoint newWaypoint(final Instant time,
            final Coordinates coordinates, final String name,
            final String description, final Set<Link> links) {
        return new WaypointBuilder(time, coordinates).name(name).description(
                description).links(links).build();

    }

    private Trail newTrail(final Set<Waypoint> points, final String name,
            final String description, final Set<Link> links, final Integer level,
            final Activity activity) {
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        return newTrail(points, name, description, links, level, activity,
                countries);
    }

    private Trail newTrail(final Set<Waypoint> points, final String name,
            final String description, final Set<Link> links, final Integer level,
            final Activity activity, final Set<String> countries) {
        return new TrailBuilder(name, points).description(description).
            links(links).difficultyRating(level).activity(activity).
            countries(countries).build();
    }
}
