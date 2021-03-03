import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.type.ImString;

public class Example extends Application {

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Test");
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
    }

    @Override
    public void process() {
        ImGui.text("Hello World");
        ImGui.inputText("label", text);
    }

    private ImString text = new ImString();

    public static void main(String[] args) {
        launch(new Example());
    }

}
