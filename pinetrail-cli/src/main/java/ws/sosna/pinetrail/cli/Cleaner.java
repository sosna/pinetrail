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
package ws.sosna.pinetrail.cli;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Formats;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.ReaderSettings;
import ws.sosna.pinetrail.api.io.ReaderSettingsBuilder;
import ws.sosna.pinetrail.api.io.Readers;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.api.io.WriterSettings;
import ws.sosna.pinetrail.api.io.WriterSettingsBuilder;
import ws.sosna.pinetrail.api.io.Writers;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/** @author Xavier Sosnovsky */
final class Cleaner implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Pinalyzr.class);

  private String inputFile;

  @Option(name = "-h", aliases = "--help", usage = "Print this message")
  private boolean help = false;

  private boolean isQuiet = false;
  private boolean keepOutliers = false;
  private boolean keepIdlePoints = false;
  private boolean prettyPrinting = false;
  private boolean groupSubTrails = false;
  private boolean writeRoute = false;
  private boolean crossBorder = false;

  Cleaner() {
    super();
  }

  @Argument(
      required = true,
      index = 0,
      metaVar = "inputFile",
      usage = "The file or directory to be processed (mandatory).")
  void setInputFile(final String inputFile) {
    this.inputFile = inputFile;
  }

  @Option(
      name = "-k",
      aliases = {"--key"},
      metaVar = "string",
      usage =
          "Your MapQuest key. This is optional but "
              + "**highly** recommended, as GPX devices quite often have "
              + "elevation data of suboptimal quality. This is a sticky option: "
              + "Once it has been set, the value will be retained for future uses.")
  void setMapQuestKey(final String key) {
    Preferences.userRoot().node("ws.sosna.pinetrail.UserSettings").put("mapQuestKey", key);
  }

  @Option(
      name = "-r",
      aliases = {"--retain"},
      metaVar = "boolean",
      usage =
          "Whether suspicious values (i.e. outliers) and inactive points"
              + " should be kept (optional). By default, they will be removed.")
  void keepAllPoints(final boolean flag) {
    this.keepOutliers = flag;
    this.keepIdlePoints = flag;
  }

  @Option(
      name = "-ro",
      aliases = {"--retain-outliers"},
      metaVar = "boolean",
      usage =
          "Whether suspicious values (i.e. outliers) "
              + "should be kept (optional). By default, outliers will be removed.")
  void keepOutliers(final boolean flag) {
    this.keepOutliers = flag;
  }

  @Option(
      name = "-ri",
      aliases = {"--retain-inactive"},
      metaVar = "boolean",
      usage =
          "Whether inactive points should be kept (optional). "
              + "By default, inactive points will be removed.")
  void keepIdlePoints(final boolean flag) {
    this.keepIdlePoints = flag;
  }

  @Option(
      name = "-i",
      aliases = {"--iterations"},
      metaVar = "integer",
      usage =
          "How many attempts should be made to identify suspicious"
              + " values. By default, 3 attempts will be made. This is a sticky "
              + "option: Once it has been set, the value will be retained for"
              + " future uses.")
  void setCleanupIterations(final String iterations) {
    Preferences.userRoot().node("ws.sosna.pinetrail.model.Trail").put("cleanupPasses", iterations);
  }

  @Option(
      name = "-v",
      aliases = {"--verbose"},
      metaVar = "boolean",
      usage =
          "Increases the amount of information displayed, while the "
              + "cleaning process runs. By default, only key information "
              + "is displayed.")
  void isVerbose(final boolean verbose) {
    final ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(ch.qos.logback.classic.Level.DEBUG);

    final ch.qos.logback.classic.Logger pinetrail =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ws.sosna.pinetrail");
    pinetrail.setLevel(ch.qos.logback.classic.Level.DEBUG);
  }

  @Option(
      name = "-p",
      aliases = {"--pretty-printing"},
      metaVar = "boolean",
      usage = "Whether the output will be beautified. By default, this is " + "not be the case.")
  void prettyPrinting(final boolean flag) {
    this.prettyPrinting = flag;
  }

  @Option(
      name = "-q",
      aliases = {"--quiet"},
      metaVar = "boolean",
      usage = "Displays only errors.")
  void isQuiet(final boolean quiet) {
    this.isQuiet = true;
    final ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(ch.qos.logback.classic.Level.ERROR);
    final ch.qos.logback.classic.Logger pinetrail =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ws.sosna.pinetrail");
    pinetrail.setLevel(ch.qos.logback.classic.Level.ERROR);
  }

  @Option(
      name = "-g",
      aliases = {"--group-subtrails"},
      metaVar = "boolean",
      usage =
          "Whether subtrails should be grouped into one trail. Certain "
              + "formats allow intermediary groupings between the trail and the "
              + "waypoints. Gpx for example has an additional level, called "
              + "segment, between tracks and waypoints. If true, these "
              + "intermediary levels will be merged into one trail.")
  void groupSubTrails(final boolean flag) {
    this.groupSubTrails = flag;
  }

  @Option(
      name = "-rt",
      aliases = {"--route"},
      metaVar = "boolean",
      usage =
          "Whether the trail should contain time information. Removing "
              + "time information can be useful, for example, when sharing an "
              + "itinerary. Defaults to false.")
  void writeRoute(final boolean flag) {
    this.writeRoute = flag;
  }

  @Option(
      name = "-x",
      aliases = {"--cross-border"},
      metaVar = "boolean",
      usage =
          "Whether the trail crosses country borders. If true, multiple "
              + "points will be selected for reverse geocoding. Defaults to "
              + "false for performance reasons.")
  void crossBorder(final boolean flag) {
    this.crossBorder = flag;
  }

  @Override
  public void run() {
    if (null == inputFile) {
      throw new IllegalArgumentException(
          "Missing file parameter. " + "Usage: TrailAnalyzer fileName");
    }
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("keepOutliers", Boolean.toString(keepOutliers));
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("keepIdlePoints", Boolean.toString(keepIdlePoints));
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("crossBorder", Boolean.toString(crossBorder));
    final Set<Path> files = getInputFiles(FileSystems.getDefault().getPath(".", inputFile));
    files.parallelStream()
        .map(path -> processJob(path))
        .filter(i -> i != null)
        .forEach(r -> handleResults(r));
  }

  private Results processJob(final Path path) {
    final ReaderSettings settings =
        new ReaderSettingsBuilder().groupSubTrails(groupSubTrails).crossBorder(crossBorder).build();
    final Formats format = Formats.of(path);
    final Reader reader = Readers.INSTANCE.newReader(format);
    if (null == reader) {
      LOGGER.error(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_FOUND.getCode(),
          "Could not find reader for " + path.toString());
      return null;
    } else {
      final Set<Trail> trails = reader.configure(settings).apply(path);
      if (0 == trails.size()) {
        return null;
      } else {
        return new Results(path, trails);
      }
    }
  }

  private void handleResults(final Results results) {
    final Writer writer = Writers.INSTANCE.newWriter(Formats.GPX_1_1);
    final WriterSettings settings =
        new WriterSettingsBuilder()
            .writeIdlePoints(keepIdlePoints)
            .writeOutliers(keepOutliers)
            .prettyPrinting(prettyPrinting)
            .writeRoute(writeRoute)
            .build();
    writer.configure(settings);

    int counter = results.trails.size() > 1 ? 1 : 0;

    for (final Trail trail : results.trails) {
      writer.accept(trail, getOutname(counter, results.path.toString()));
      counter++;
    }

    if (!isQuiet) {
      for (final Trail trail : results.trails) {
        new TrailInfoWriter().write(trail);
      }
    }
  }

  private Set<Path> getInputFiles(final Path path) {
    final Set<Path> files = new LinkedHashSet<>();
    if (Files.isDirectory(path)) {
      try {
        Files.list(path).forEach(p -> files.addAll(getInputFiles(p)));
      } catch (final IOException ex) {
        LOGGER.error(
            Markers.IO.getMarker(),
            "{} | {} | {}.",
            Actions.GET,
            StatusCodes.INTERNAL_ERROR.getCode(),
            "Could not get files from " + path.toString());
      }
    } else if (Files.isRegularFile(path)) {
      files.add(path);
    } else if (Files.notExists(path)) {
      LOGGER.error(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.GET,
          StatusCodes.NOT_FOUND.getCode(),
          "Could not get file " + path.toString());
    }
    return files;
  }

  private Path getOutname(final int counter, final String path) {
    final int extPos = path.lastIndexOf('.');
    final String basename = path.substring(0, extPos);
    final String ext = extPos > 0 ? path.substring(extPos + 1) : "";
    final StringBuilder location = new StringBuilder();
    location.append(basename);
    location.append("_clean");
    if (counter > 0) {
      location.append('.');
      location.append(counter);
    }
    if (!ext.isEmpty()) {
      location.append('.');
      location.append(ext);
    }
    return FileSystems.getDefault().getPath(".", location.toString());
  }

  private static final class Results {

    private final Path path;
    private final Set<Trail> trails;

    Results(final Path path, final Set<Trail> trails) {
      super();
      this.path = path;
      this.trails = trails;
    }
  }
}
