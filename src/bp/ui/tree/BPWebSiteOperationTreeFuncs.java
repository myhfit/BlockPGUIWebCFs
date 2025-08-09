package bp.ui.tree;

import java.util.List;

import bp.ui.scomp.BPTree.BPTreeNode;
import bp.web.BPWebOperation;

public class BPWebSiteOperationTreeFuncs extends BPTreeFuncsObject
{
	public BPWebSiteOperationTreeFuncs(List<BPWebOperation> ops)
	{
		super(ops);
	}

	public List<?> getRoots()
	{
		return (List<?>) m_root;
	}

	public List<?> getChildren(BPTreeNode node, boolean isdelta)
	{
		Object data = node.getUserObject();
		if (data instanceof BPWebOperation)
		{
			BPWebOperation op = (BPWebOperation) data;
			return super.getChildren(op.getMappedData());
		}
		return super.getChildren(node, isdelta);
	}

	public void setWebOperations(List<BPWebOperation> ops)
	{
		m_root = ops;
	}

	public boolean isLeaf(BPTreeNode node)
	{
		Object obj = node.getUserObject();
		if (obj == null)
			return false;
		if (obj instanceof BPWebOperation)
			return false;
		return super.isLeaf(node);
	}
}
