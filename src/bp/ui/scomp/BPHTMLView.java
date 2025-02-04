package bp.ui.scomp;

import javax.swing.text.DefaultCaret;

public class BPHTMLView extends BPHTMLPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2184904245683272975L;
	
	public BPHTMLView()
	{
		setEditable(false);
		((DefaultCaret) getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
	}
}
