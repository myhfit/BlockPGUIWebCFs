package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.BPCore;
import bp.config.UIConfigs;
import bp.context.BPProjectsContext;
import bp.project.BPResourceProject;
import bp.project.BPResourceProjectWebSite;
import bp.res.BPResourceWebSiteLink;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.editor.BPWebSiteOperationPanel;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.scomp.BPList.BPListRenderer;
import bp.ui.util.UIUtil;
import bp.web.BPWebContext;
import bp.web.BPWebContextBase;
import bp.web.BPWebOperation;
import bp.web.BPWebResponse;

public class BPWebSiteOperationResultPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8438029685041700877L;

	protected BPWebSiteOperationPanel m_wspan;
	protected BPComboBox<BPResourceWebSiteLink> m_cmbwslink;
	protected List<BPWebResponse> m_resps = new ArrayList<BPWebResponse>();
	protected BPList<BPWebResponse> m_lstresp;
	protected BPWebResponseContentPane m_pnlcontent;

	public BPWebSiteOperationResultPane(BPWebSiteOperationPanel wspan)
	{
		m_wspan = wspan;
		init();
	}

	protected void init()
	{
		BPToolBarSQ actionbar = new BPToolBarSQ();
		JPanel pantop = new JPanel();
		JPanel panwslink = new JPanel();
		BPLabel lblwslink = new BPLabel("@");
		BPSplitPane panmain = new BPSplitPane(BPSplitPane.HORIZONTAL_SPLIT);
		JScrollPane scrollresp = new JScrollPane();
		m_lstresp = new BPList<BPWebResponse>();
		m_cmbwslink = new BPComboBox<BPResourceWebSiteLink>();
		m_resps = new ArrayList<BPWebResponse>();
		m_pnlcontent = new BPWebResponseContentPane();
		Action actrun = BPAction.build("Run").callback(this::onRun).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).tooltip("Run(F5)").vIcon(BPIconResV.START()).getAction();
		actionbar.setActions(new Action[] { actrun });

		lblwslink.setLabelFont();
		m_lstresp.setMonoFont();
		m_cmbwslink.setListFont();
		m_cmbwslink.replaceWBorder();
		m_cmbwslink.setPreferredSize(new Dimension(UIUtil.scale(120), 0));
		m_cmbwslink.setFocusable(false);
		BPListModel<BPWebResponse> model = new BPListModel<BPWebResponse>();
		model.setDatas(m_resps);
		m_lstresp.setModel(model);
		m_lstresp.setCellRenderer(new BPWebResponseRenderer());
		m_lstresp.addListSelectionListener(this::onResponseSelected);
		actionbar.setBarHeight(20);
		actionbar.setPreferredSize(null);
		pantop.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));
		scrollresp.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		panmain.setDividerLocation(400);
		panmain.setDividerSize(4);
		panmain.setBorder(new EmptyBorder(0, 0, 0, 0));
		panmain.setDividerBorderColor(UIConfigs.COLOR_STRONGBORDER(), false);

		initWebSites();

		actionbar.add(Box.createRigidArea(new Dimension(1, 20)));
		panwslink.setLayout(new BorderLayout());
		pantop.setLayout(new BorderLayout());

		setLayout(new BorderLayout());
		panwslink.add(lblwslink, BorderLayout.WEST);
		panwslink.add(m_cmbwslink, BorderLayout.CENTER);
		pantop.add(actionbar, BorderLayout.WEST);
		pantop.add(panwslink, BorderLayout.EAST);
		scrollresp.setViewportView(m_lstresp);
		panmain.setLeftComponent(scrollresp);
		panmain.setRightComponent(m_pnlcontent);
		add(pantop, BorderLayout.NORTH);
		add(panmain, BorderLayout.CENTER);
	}

	protected static class BPWebResponseRenderer extends BPListRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2105730686881914319L;

		public BPWebResponseRenderer()
		{
			super((resp) -> resp.toString());
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			BPWebResponse resp = (BPWebResponse) value;
			if (resp.responsecode != 200)
			{
				if (resp.responsecode >= 400)
				{
					c.setForeground(UIUtil.mix(isSelected ? UIConfigs.COLOR_TEXTBG() : UIConfigs.COLOR_TEXTFG(), Color.RED, 255));
				}
			}
			return c;
		}
	}

	protected void initWebSites()
	{
		List<BPResourceWebSiteLink> ws = new ArrayList<BPResourceWebSiteLink>();
		BPProjectsContext context = BPCore.getProjectsContext();
		BPResourceProject[] prjs = context.listProject();
		for (BPResourceProject prj : prjs)
		{
			if (prj instanceof BPResourceProjectWebSite)
			{
				ws.addAll(((BPResourceProjectWebSite) prj).listWebSiteLink());
			}
		}
		BPComboBoxModel<BPResourceWebSiteLink> model = new BPComboBoxModel<BPResourceWebSiteLink>();
		model.setDatas(ws);
		m_cmbwslink.setModel(model);
	}

	protected void onRun(ActionEvent e)
	{
		m_resps.clear();
		m_pnlcontent.setWebResponse(null);
		m_lstresp.clearSelection();
		m_lstresp.updateUI();
		List<BPWebOperation> ops = m_wspan.getWebOperations();
		runWebOperations(ops);
	}

	protected BPResourceWebSiteLink getWebSiteLink()
	{
		return (BPResourceWebSiteLink) m_cmbwslink.getSelectedItem();
	}
	
	public void setWebSiteLink(BPResourceWebSiteLink wslink)
	{
		m_cmbwslink.setSelectedItem(wslink);
	}

	protected BPWebContext createWebContext()
	{
		BPResourceWebSiteLink link = getWebSiteLink();
		if (link != null)
			return new BPWebContextBase(link);
		return null;
	}

	protected boolean runWebOperations(List<BPWebOperation> ops)
	{
		boolean rc = false;
		BPWebContext context = createWebContext();
		if (context != null)
		{
			for (BPWebOperation op : ops)
			{
				context.operate(op).whenComplete(this::onResult);
			}
			context.shutdown();
			return true;
		}
		return rc;
	}

	protected void addResponse(BPWebResponse resp)
	{
		m_resps.add(resp);
		m_lstresp.updateUI();
	}

	protected void onResult(BPWebResponse resp, Throwable err)
	{
		BPWebResponse r = resp;
		if (r != null)
		{
			UIUtil.laterUI(() ->
			{
				addResponse(resp);
			});
		}
	}

	protected void onResponseSelected(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			showResponse(m_lstresp.getSelectedValue());
		}
	}

	protected void showResponse(BPWebResponse response)
	{
		m_pnlcontent.setWebResponse(response);
	}

	public Object getResult(String format)
	{
		BPWebResponse resp = m_pnlcontent.getWebResponse();
		if (resp != null)
		{
			return resp.getText();
		}
		return null;
	}
}
