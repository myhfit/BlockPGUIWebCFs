package bp.ext;

import bp.context.BPFileContext;
import bp.locale.BPLocaleHelpers;
import bp.ui.actions.BPActionHelperWebCFs;

public class BPExtensionLoaderGUIWebCFs implements BPExtensionLoaderGUISwing
{
	public String getName()
	{
		return "WebCommonFormats GUI-Swing";
	}

	public String[] getParentExts()
	{
		return new String[] {"GUI-Swing","WebCommonFormats"};
	}
	
	public void install(BPFileContext context)
	{
		BPLocaleHelpers.registerHelper(new BPActionHelperWebCFs());
	}

	public String[] getDependencies()
	{
		return null;
	}
}
