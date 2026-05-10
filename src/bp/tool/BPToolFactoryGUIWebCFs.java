package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;
import bp.ui.actions.BPActionConstCommon;

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
		installfunc.accept(BPActionConstCommon.TXT_WEB.text(), new BPToolGUIWebSearchEngine());
		installfunc.accept(BPActionConstCommon.TXT_NETWORK.text(), new BPToolGUINetAddress());
	}
}
