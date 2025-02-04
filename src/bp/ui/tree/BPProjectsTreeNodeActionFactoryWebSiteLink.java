package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.Action;

import bp.res.BPResource;
import bp.res.BPResourceWebSiteLink;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.actions.BPProjectsTreeNodeActionsWebSiteLink;
import bp.ui.scomp.BPTree;

public class BPProjectsTreeNodeActionFactoryWebSiteLink implements BPProjectsTreeNodeActionFactory
{
	protected BPPathTreeNodeActions m_actptree = new BPPathTreeNodeActions();
	protected BPProjectsTreeNodeActionsWebSiteLink m_actptreeext = new BPProjectsTreeNodeActionsWebSiteLink();

	public List<Action> getActions(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		List<Action> rc = null;
		if (res instanceof BPResourceWebSiteLink)
		{
			rc = new ArrayList<Action>();
			rc.add(m_actptreeext.getNewAction(tree, (BPResourceWebSiteLink) res, channelid));
			rc.add(BPAction.separator());
			rc.add(m_actptree.getDeleteResAction(tree, res, channelid));
		}
		return rc;
	}

	public void register(BiConsumer<String, BPProjectsTreeNodeActionFactory> regfunc)
	{
		regfunc.accept(BPResourceWebSiteLink.class.getName(), this);
	}
}