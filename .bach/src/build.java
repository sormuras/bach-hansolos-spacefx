import com.github.sormuras.bach.Bach;
import com.github.sormuras.bach.Configuration;
import com.github.sormuras.bach.Project;
import com.github.sormuras.bach.ToolCall;
import com.github.sormuras.bach.project.DeclaredFolders;
import com.github.sormuras.bach.project.DeclaredModule;
import com.github.sormuras.bach.project.ExternalModuleLocator;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.util.Map;

class build {
  public static void main(String... args) {
    var spacefx =
        new DeclaredModule(
            Path.of(""),
            Path.of("src/main/java/module-info.java"),
            ModuleDescriptor.newModule("eu.hansolo.spacefx")
                .requires("javafx.base")
                .requires("javafx.controls")
                .requires("javafx.graphics")
                .requires("javafx.media")
                .build(),
            DeclaredFolders.of(Path.of("src/main/java"))
                .withResourcePath(Path.of("src/main/resources")),
            Map.of());

    var javafx = Path.of(".bach/external-modules/javafx@18.0.1/javafx@18.0.1.properties");
    var project = Project.ofDefaults();
    project =
        project
            .withVersion("17-bach")
            .withModule(project.spaces().main(), spacefx)
            .with(ExternalModuleLocator.PropertiesBundleModuleLocator.of(javafx));

    var bach = new Bach(Configuration.ofDefaults(), project);
    bach.run("com.github.sormuras.bach/build");

    linkCustomRuntimeImage(bach);
  }

  static void linkCustomRuntimeImage(Bach bach) {
    var main = bach.project().spaces().main();
    var test = bach.project().spaces().test();
    var paths = bach.configuration().paths();
    var image = paths.out(main.name(), "image");
    bach.run("tree", "--mode", "CLEAN", image);
    bach.run(
        ToolCall.of("jlink")
            .with("--output", image)
            .with("--launcher", "spacefx=eu.hansolo.spacefx/eu.hansolo.spacefx.SpaceFX")
            .with("--add-modules", main.modules().names(","))
            .with("--module-path", test.toModulePath(paths).orElseThrow()));
  }
}
