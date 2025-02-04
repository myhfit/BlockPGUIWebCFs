package bp.tool;

import java.awt.Container;

import bp.ui.editor.BPWebSearchEnginePanel;
import bp.ui.frame.BPFrame;
import bp.ui.util.UIUtil;

public class BPToolGUIWebSearchEngine extends BPToolGUIBase<BPToolGUIWebSearchEngine.BPToolGUIContextWebSearchEngine>
{
	public String getName()
	{
		return "Search Engines";
	}

	protected BPToolGUIContextWebSearchEngine createToolContext()
	{
		return new BPToolGUIContextWebSearchEngine();
	}

	protected void setFramePrefers(BPFrame f)
	{
		f.setPreferredSize(UIUtil.getPercentDimension(0.8f, 0.8f));
		f.pack();
		if (!f.isLocationByPlatform())
			f.setLocationRelativeTo(null);
	}

	protected static class BPToolGUIContextWebSearchEngine implements BPToolGUIBase.BPToolGUIContext
	{
		public void initUI(Container par, Object... params)
		{
			par.add(new BPWebSearchEnginePanel());
		}

		public void initDatas(Object... params)
		{
		}
	}
}
