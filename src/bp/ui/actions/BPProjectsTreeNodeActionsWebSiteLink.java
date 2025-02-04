package bp.ui.actions;

import java.awt.event.KeyEvent;

import javax.swing.Action;

import bp.BPGUICore;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceWebSiteLink;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.tree.BPTreeComponent;

public class BPProjectsTreeNodeActionsWebSiteLink
{
	public final static String ACTION_NEWFILEUNSAVED = "newfileunsaved";

	public BPProjectsTreeNodeActionsWebSiteLink()
	{
	}

	public BPAction getNewAction(BPTreeComponent<BPTree> tree, BPResourceWebSiteLink res, int channelid)
	{
		BPAction rc = BPAction.build("New").mnemonicKey(KeyEvent.VK_N).getAction();
		BPResourceFileLocal f = new BPResourceFileLocal("untitled.wsconsole");
		BPAction actneweditor = BPAction.build("WebSite Console").callback((e) ->
		{
			BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWFILEUNSAVED, f, res));
		}).mnemonicKey(KeyEvent.VK_S).getAction();
		rc.putValue(BPAction.SUB_ACTIONS, new Action[] { actneweditor });
		return rc;
	}
}
