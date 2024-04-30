import java.nio.file.Path;
import run.bach.workflow.Builder;
import run.bach.workflow.Structure;
import run.bach.workflow.Workflow;

record Project(Workflow workflow) implements Builder {
  static Project ofCurrentWorkingDirectory() {
    return new Project(
        Workflow.ofCurrentWorkingDirectory()
            .withMainSpace(
                main ->
                    main.with(
                            new Structure.DeclaredModule(
                                    Path.of("src"), Path.of("src/main/java/module-info.java"))
                                .withResourcePath(Path.of("src/main/resources")))
                        .withLauncher("spacefx=eu.hansolo.spacefx/eu.hansolo.spacefx.SpaceFX")
                        .withCompileRuntimeImage()));
  }

  @Override
  public boolean builderDoesCleanAtTheBeginning() {
    return true;
  }
}
