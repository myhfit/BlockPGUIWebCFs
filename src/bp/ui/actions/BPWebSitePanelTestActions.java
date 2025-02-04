package bp.ui.actions;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import bp.ui.editor.BPWebSitePanel;
import bp.ui.res.icon.BPIconResV;

public class BPWebSitePanelTestActions
{
	protected BPWebSitePanel m_pnl;

	public Action testrequest;
	public Action addrequest;

	public BPWebSitePanelTestActions(BPWebSitePanel pnl)
	{
		m_pnl = pnl;
		testrequest = BPAction.build("Test").callback((e) -> m_pnl.test()).vIcon(BPIconResV.START()).tooltip("Test Request").acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).getAction();
		addrequest = BPAction.build("Add").callback((e) -> m_pnl.add()).vIcon(BPIconResV.ADD()).getAction();
	}
}
