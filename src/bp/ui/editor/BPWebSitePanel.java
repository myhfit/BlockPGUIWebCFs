package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.context.BPProjectsContext;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.data.BPXData;
import bp.data.BPXYDData;
import bp.format.BPFormat;
import bp.format.BPFormatWebSiteConsole;
import bp.project.BPResourceProject;
import bp.project.BPResourceProjectWebSite;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceWebSiteLink;
import bp.ui.BPViewer;
import bp.ui.actions.BPWebSitePanelTestActions;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPSplitPane;
import bp.ui.scomp.BPTextField;
import bp.ui.tree.BPTreeCellRendererObject;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPWebSiteOperationTreeFuncs;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.WebUtil;
import bp.web.BPWebContext;
import bp.web.BPWebContextBase;
import bp.web.BPWebOperation;
import bp.web.BPWebOperationBase;
import bp.web.BPWebResponse;

public class BPWebSitePanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPTextContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -570025935310939011L;

	protected BPWebContext m_context;
	protected BPCodePane m_result;
	protected JPanel m_toppanel;
	protected BPList<String> m_cmds;
	protected BPTextField m_txtpath;
	protected BPComboBox<String> m_cmbverb;
	protected BPComboBox<BPResourceWebSiteLink> m_cmbws;
	protected BPResourceWebSiteLink m_wslink;
	protected BPLabel m_lblwb;
	protected BPTreeComponentBase m_treeops;

	protected BiConsumer<List<BPXData>, Integer> m_adddatafunc;
	protected Consumer<BPXYDData> m_setupqueryfunc;

	protected BPSplitPane m_sp;

	protected BPTextContainer m_con;
	protected int m_channelid;
	protected String m_id;
	protected boolean m_needsave;
	protected List<BPWebOperation> m_ops = new ArrayList<BPWebOperation>();
	protected JScrollPane m_scrolltree;

	protected BiConsumer<String, Boolean> m_statechanged;

	public BPWebSitePanel()
	{
		init();
	}

	protected void init()
	{
		m_toppanel = new JPanel();
		m_result = new BPCodePane();
		m_cmbverb = new BPComboBox<String>();
		m_cmbws = new BPComboBox<BPResourceWebSiteLink>();
		m_txtpath = new BPTextField();
		m_treeops = new BPTreeComponentBase();
		BPLabel lblpath = new BPLabel(" Path:");
		BPLabel lblverb = new BPLabel("Verb:");
		JPanel tp = new JPanel();
		JPanel navbar = new JPanel();
		JPanel panws = new JPanel();
		JPanel pancmd = new JPanel();
		JPanel panpath = new JPanel();
		m_sp = new BPSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		m_scrolltree = new JScrollPane();
		BPToolBarSQ actionbar = new BPToolBarSQ();
		m_lblwb = new BPLabel(" @");
		BPWebSitePanelTestActions acts = new BPWebSitePanelTestActions(this);
		actionbar.setActions(new Action[] { acts.testrequest, acts.addrequest });

		m_cmbverb.setListFont();
		m_cmbws.setListFont();
		m_txtpath.setMonoFont();
		lblverb.setLabelFont();
		lblpath.setLabelFont();
		m_lblwb.setLabelFont();
		m_treeops.setTreeFont();

		m_toppanel.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_cmbverb.replaceWBorder();
		m_cmbws.replaceWBorder();
		m_cmbverb.setPreferredSize(new Dimension(UIUtil.scale(80), (int) m_cmbverb.getPreferredSize().getHeight()));
		m_cmbws.setPreferredSize(new Dimension(UIUtil.scale(80), (int) m_cmbws.getPreferredSize().getHeight()));
		m_cmbws.addItemListener(this::onWSChanged);
		m_txtpath.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));
		m_sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_sp.setDividerLocation((int) (200 * UIConfigs.UI_SCALE()));
		int dividersize = UIConfigs.DIVIDER_SIZE();
		m_sp.setDividerSize(UIConfigs.DIVIDER_SIZE());
		if (dividersize > 1)
		{
			m_scrolltree.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
			m_sp.setDividerBorderColor(UIConfigs.COLOR_WEAKBORDER(), false);
		}
		else
		{
			m_scrolltree.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_sp.setDividerBorderColor(UIConfigs.COLOR_WEAKBORDER(), true);
		}
		m_treeops.setRootVisible(false);
		navbar.setMinimumSize(new Dimension(0, UIUtil.scale(20)));
		navbar.setPreferredSize(new Dimension(2000, UIUtil.scale(20)));
		m_cmbws.setPreferredSize(new Dimension(UIUtil.scale(120), 0));
		actionbar.setBarHeight(20);
		actionbar.setPreferredSize(null);

		initVerbs();
		initWebSites();
		m_treeops.setTreeFuncs(new BPWebSiteOperationTreeFuncs(m_ops));
		m_treeops.setCellRenderer(new BPTreeCellRendererObject());

		pancmd.setLayout(new BorderLayout());
		panpath.setLayout(new BorderLayout());
		panws.setLayout(new BorderLayout());
		tp.setLayout(new GridLayout(1, 2, 0, 0));
		setLayout(new BorderLayout());
		m_toppanel.setLayout(new BoxLayout(m_toppanel, BoxLayout.Y_AXIS));
		navbar.setLayout(new BorderLayout());

		m_scrolltree.setViewportView(m_treeops);
		pancmd.add(actionbar, BorderLayout.WEST);
		pancmd.add(m_cmbverb, BorderLayout.CENTER);
		panpath.add(lblpath, BorderLayout.WEST);
		panpath.add(m_txtpath, BorderLayout.CENTER);
		panws.add(m_lblwb, BorderLayout.WEST);
		panws.add(m_cmbws, BorderLayout.CENTER);
		navbar.add(pancmd, BorderLayout.WEST);
		navbar.add(panpath, BorderLayout.CENTER);
		navbar.add(panws, BorderLayout.EAST);
		m_toppanel.add(navbar);
		m_sp.setLeftComponent(m_scrolltree);
		m_sp.setRightComponent(tp);
		add(m_sp, BorderLayout.CENTER);
		add(m_toppanel, BorderLayout.NORTH);

		UIUtil.laterUI(() -> m_result.resizeDoc());
	}

	protected void onWSChanged(ItemEvent e)
	{
		if (e.getStateChange() == ItemEvent.SELECTED)
		{
			BPResourceWebSiteLink link = (BPResourceWebSiteLink) e.getItem();
			m_wslink = link;
			m_context = link == null ? null : new BPWebContextBase(link);
			initDatas();
		}
	}

	protected void initVerbs()
	{
		List<String> verbs = Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");
		BPComboBoxModel<String> model = new BPComboBoxModel<String>();
		model.setDatas(verbs);
		m_cmbverb.setModel(model);
		m_cmbverb.setSelectedIndex(0);
		m_cmbverb.setEditable(true);
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
		m_cmbws.setModel(model);
	}

	protected void initDatas()
	{
		m_txtpath.setText("/");
		BiConsumer<String, Boolean> statechanged = m_statechanged;
		if (statechanged != null)
		{
			statechanged.accept(m_id, m_needsave);
		}
	}

	public void setContextByWebSiteLink(BPResourceWebSiteLink link)
	{
		m_cmbws.setSelectedItem(link);
	}

	public void test()
	{
		if (m_context == null)
		{
			UIStd.err(new RuntimeException("Select WebSite First"));
			return;
		}
		String verb = m_cmbverb.getText();
		String path = m_txtpath.getText();
		BPWebOperation op = BPWebOperationBase.build(verb).setPath(path).setHeaderFields(WebUtil.getDefaultHeaderFields()).getOperation();
		m_context.operate(op).whenComplete(this::onRunComplete);
	}

	public void add()
	{
		if (m_context == null)
		{
			UIStd.err(new RuntimeException("Select WebSite First"));
			return;
		}
		String verb = m_cmbverb.getText();
		String path = m_txtpath.getText();
		BPWebOperation op = BPWebOperationBase.build(verb).setPath(path).setHeaderFields(WebUtil.getDefaultHeaderFields()).getOperation();
		m_ops.add(op);
		m_treeops.reloadModel();
	}

	protected void onRunComplete(BPWebResponse resp, Throwable e)
	{
		if (e != null)
		{
			UIStd.err(e);
		}
		else
		{
			UIStd.textarea(resp.getText(), "");
		}
	}

	public final static class BPEditorFactoryWebSitePanel implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatWebSiteConsole.FORMAT_WEBSITECONSOLE };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			BPWebSitePanel rc = new BPWebSitePanel();
			if (params != null && params.length > 0)
				rc.setContextByWebSiteLink((BPResourceWebSiteLink) params[0]);
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				BPTextContainer con = new BPTextContainerBase();
				con.bind(res);
				((BPWebSitePanel) editor).bind(con, ((BPResourceFileSystem) res).getTempID() != null);
			}
		}

		public String getName()
		{
			return "WebSite Console";
		}
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void bind(BPTextContainer con, boolean noread)
	{

		m_con = con;
		if (!noread)
		{
			m_con.open();

			m_con.close();
		}
	}

	public void unbind()
	{
		m_con.close();
		m_con = null;
	}

	public BPTextContainer getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	public void save()
	{
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return m_needsave;
	}

	public void setNeedSave(boolean needsave)
	{
		m_needsave = needsave;
		BiConsumer<String, Boolean> statechanged = m_statechanged;
		if (statechanged != null)
		{
			statechanged.accept(m_id, m_needsave);
		}
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> cb)
	{
		m_statechanged = cb;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}
}