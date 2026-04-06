package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;

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
		installfunc.accept(BPActionHelpers.getValue(BPActionConstCommon.TXT_WEB, null, null), new BPToolGUIWebSearchEngine());
		installfunc.accept(BPActionHelpers.getValue(BPActionConstCommon.TXT_NETWORK, null, null), new BPToolGUINetAddress());
	}
}
