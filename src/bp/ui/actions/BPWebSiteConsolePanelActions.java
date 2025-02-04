package bp.ui.actions;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import bp.ui.editor.BPWebSiteConsolePanel;
import bp.ui.res.icon.BPIconResV;

public class BPWebSiteConsolePanelActions
{
	protected BPWebSiteConsolePanel m_pnl;

	public Action testrequest;
	public Action addrequest;

	public BPWebSiteConsolePanelActions(BPWebSiteConsolePanel pnl)
	{
		m_pnl = pnl;
		testrequest = BPAction.build("Test").callback((e) -> m_pnl.test()).vIcon(BPIconResV.START()).tooltip("Test Request").acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).getAction();
		addrequest = BPAction.build("Add").callback((e) -> m_pnl.add()).vIcon(BPIconResV.ADD()).getAction();
	}
}
