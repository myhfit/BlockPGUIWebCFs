package bp.ui.shortcut;

import java.util.function.BiConsumer;

public class BPShortCutFactoryWebCFs implements BPShortCutFactory
{
	public void register(BiConsumer<String, BPShortCutFactory> regfunc)
	{
		regfunc.accept(BPShortCutBrowser.SCKEY_BROWSER, this);
	}

	public BPShortCut createShortCut(String key)
	{
		BPShortCut rc = null;
		switch (key)
		{
			case BPShortCutBrowser.SCKEY_BROWSER:
			{
				rc = new BPShortCutBrowser();
				break;
			}
		}
		return rc;
	}
}
