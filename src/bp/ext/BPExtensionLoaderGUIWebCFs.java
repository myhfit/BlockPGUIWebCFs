package bp.ext;

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

	public String[] getDependencies()
	{
		return null;
	}
}
