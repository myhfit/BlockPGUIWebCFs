package bp.ui.form;

import java.awt.Component;
import java.awt.Window;
import java.util.Map;

import bp.BPCore;
import bp.project.BPResourceProject;
import bp.project.BPResourceProjectWebSite;
import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceWebSiteLink;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.dialog.BPDialogSelectResourceDir;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;

public class BPFormPanelTaskWebSiteOperation extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5982166258950323876L;

	protected BPTextField m_txtwslink;
	protected BPTextField m_txtwsop;
	protected BPTextFieldPane m_panwslink;
	protected BPTextFieldPane m_panwsop;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("wslinkfilename", m_txtwslink.getText());
		rc.put("wsopfilename", m_txtwsop.getText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_panwslink = makeSingleLineTextFieldPanel(this::onSelectWSLink);
		m_txtwslink = m_panwslink.getTextComponent();

		m_panwsop = makeSingleLineTextFieldPanel(this::onSelectWSOP);
		m_txtwsop = m_panwsop.getTextComponent();

		addLine(new String[] { "WebSite" }, new Component[] { m_panwslink }, () -> !m_txtwslink.isEmpty());
		addLine(new String[] { "Operation File" }, new Component[] { m_panwsop }, () -> !m_txtwsop.isEmpty());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtwslink, data, "wslinkfilename", editable);
		setComponentValue(m_txtwsop, data, "wsopfilename", editable);
	}

	protected String onSelectWSLink(String oldpath)
	{
		String rc = null;
		BPDialogSelectResourceDir dlg = new BPDialogSelectResourceDir();
		dlg.setProjectResource(true);
		dlg.setFilter(this::checkWSLink);
		dlg.setVisible(true);
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			BPResourceFile fres = (BPResourceFile) ((BPResourceWebSiteLink) res).getRawResource();
			rc = BPCore.getFileContext().comparePath(fres.getFileFullName());
		}
		return rc;
	}

	protected boolean checkWSLink(BPResource res)
	{
		if (res instanceof BPResourceProject)
		{
			if (res instanceof BPResourceProjectWebSite)
				return true;
			return false;
		}
		if (res.getResType() == BPResourceWebSiteLink.RESTYPE_WEBSITELINK)
		{
			return true;
		}
		else if (res.isLeaf())
		{
			return false;
		}
		return true;
	}

	protected boolean checkWSOP(BPResource res)
	{
		if (res.isFileSystem())
		{
			BPResourceFileSystem fs = (BPResourceFileSystem) res;
			if (fs.isFile())
			{
				if (!(".wsop".equals(fs.getExt())))
					return false;
			}
		}
		return true;

	}

	protected String onSelectWSOP(String oldpath)
	{
		String rc = null;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2((Window) getFocusCycleRootAncestor());
		dlg.setFilter(this::checkWSOP);
		dlg.showOpen();
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			rc = BPCore.getFileContext().comparePath(((BPResourceFile) res).getFileFullName());
		}
		return rc;
	}
}
