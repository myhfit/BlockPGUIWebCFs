package bp.ui.scomp;

import javax.swing.text.EditorKit;

public class BPHTMLPane extends BPTextPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1819021521195619668L;

	public BPHTMLPane()
	{
		setContentType("text/html");
	}

	public void setHTML(String html)
	{
		setText(html);
	}

	public void setHTMLFont()
	{
	}

	protected EditorKit createEditorKit()
	{
		BPHTMLEditorKit rc = new BPHTMLEditorKit();
		return rc;
	}
}
