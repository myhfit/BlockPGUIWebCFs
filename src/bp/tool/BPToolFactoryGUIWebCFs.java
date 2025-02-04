package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;

public class BPToolFactoryGUIWebCFs implements BPToolFactory
{
	public String getName()
	{
		return "GUIWebCFs";
	}

	public boolean canRunAt(BPPlatform platform)
	{
		return platform == BPPlatform.GUI_SWING;
	}

	public void install(BiConsumer<String, BPTool> installfunc, BPPlatform platform)
	{
		installfunc.accept("Web", new BPToolGUIWebSearchEngine());
		installfunc.accept("Network", new BPToolGUINetAddress());
	}
}
