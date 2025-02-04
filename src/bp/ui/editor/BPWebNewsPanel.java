package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.client.BPClientManager;
import bp.client.BPClientWebNews;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPXData;
import bp.data.BPXData.BPXDataArray;
import bp.data.BPXYDData;
import bp.data.BPXYDDataBase;
import bp.format.BPFormat;
import bp.format.BPFormatUnknown;
import bp.res.BPResource;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPXYDataCloneActions;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPTable.BPTableRendererMultiline;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.table.BPTableFuncsMapMethod;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.DateUtil;
import bp.util.ObjUtil;

public class BPWebNewsPanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8836315447406028912L;

	protected BPTable<Map<String, Object>> m_tabresult;
	protected BPTableFuncsWebNewsResult m_resultfuncs;
	protected BPComboBox<String> m_cmbses;
	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;

	public BPWebNewsPanel()
	{
		initUIComponents();
		initDatas();
	}

	protected void initUIComponents()
	{
		JPanel sp = new JPanel();
		BPToolBarSQ toolbar = new BPToolBarSQ();
		m_tabresult = new BPTable<Map<String, Object>>();
		m_resultfuncs = new BPTableFuncsWebNewsResult();
		BPTableModel<Map<String, Object>> model = new BPTableModel<Map<String, Object>>(m_resultfuncs);
		m_cmbses = new BPComboBox<String>();
		Action actrun = BPAction.build("DL").tooltip("Download").vIcon(BPIconResV.TODOWN()).callback(this::onFetch).getAction();
		Action actclone = BPAction.build("Clone").tooltip("Clone").vIcon(BPIconResV.CLONE()).callback(this::onShowClone).getAction();
		BPToolVIconButton btnsearch = new BPToolVIconButton(actrun);
		BPToolVIconButton btnclone = new BPToolVIconButton(actclone);
		JScrollPane scroll = new JScrollPane();

		toolbar.setBarHeight(UIConfigs.TEXTFIELD_HEIGHT() + 4);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_resultfuncs.setColumns(new String[] { "title+content", "datestr" }, null);
		m_resultfuncs.setColumnMethod(0, this::renderNews);
		m_cmbses.setListFont();
		m_cmbses.replaceWBorder();

		m_tabresult.setTableFont();
		m_tabresult.setModel(model);
		BPTableRendererMultiline render1 = new BPTableRendererMultiline();
		render1.setDecideRowHeight(true);
		m_tabresult.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		m_tabresult.getColumnModel().getColumn(0).setCellRenderer(render1);
		m_tabresult.getColumnModel().getColumn(1).setMaxWidth(80);

		toolbar.add(btnsearch);
		toolbar.add(btnclone);
		toolbar.add(Box.createRigidArea(new Dimension(2, 1)));
		toolbar.add(m_cmbses);
		toolbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));

		setLayout(new BorderLayout());
		sp.setLayout(new BorderLayout());
		scroll.setViewportView(m_tabresult);
		sp.add(scroll, BorderLayout.CENTER);
		add(sp, BorderLayout.CENTER);
		add(toolbar, BorderLayout.NORTH);
	}

	protected String renderNews(Map<String, Object> news)
	{
		String title = ObjUtil.toString(news.get("title"), "");
		String content = ObjUtil.toString(news.get("content"), "");

		StringBuilder sb = new StringBuilder();
		if (title.length() > 0)
		{
			sb.append("<strong>");
			sb.append(title);
			sb.append("</strong>");
			if (!(title.endsWith("<br>") || title.endsWith("<br />") || title.endsWith("<br/>")))
				sb.append("<br />");
			sb.append(content);
			return sb.toString();
		}
		else
		{
			return content;
		}
	}

	protected void onKWKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			e.consume();
			onFetch(null);
		}
	}

	protected void initDatas()
	{
		List<String> ses = BPClientManager.listNameByCategory(BPClientWebNews.CATEGORY_WEBNEWS);
		BPComboBoxModel<String> model = new BPComboBoxModel<String>();
		model.setDatas(ses);
		m_cmbses.setModel(model);
		if (ses.size() > 0)
			m_cmbses.setSelectedIndex(0);
	}

	protected void onFetch(ActionEvent e)
	{
		if (m_cmbses.getSelectedIndex() == -1)
			return;
		String enginename = (String) m_cmbses.getSelectedItem();
		List<Map<String, Object>> results = UIUtil.block(() -> CompletableFuture.supplyAsync(() ->
		{
			BPClientWebNews client = BPClientManager.get(enginename, null);
			return client.getNews();
		}), "Downloading...");
		BPTableModel<Map<String, Object>> model = m_tabresult.getBPTableModel();
		model.setDatas(results);
		model.fireTableDataChanged();
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
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

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public String getEditorName()
	{
		return "Web News";
	}

	public void onShowClone(ActionEvent e)
	{
		List<Map<String, Object>> datas = m_tabresult.getBPTableModel().getDatas();
		BPXYDData xydata = createCloneData(datas);
		Action[] acts = BPXYDataCloneActions.getActions(xydata, null);
		if (acts != null && acts.length > 0)
		{
			JPopupMenu pop = new JPopupMenu();
			JComponent[] comps = UIUtil.makeMenuItems(acts);
			for (JComponent comp : comps)
			{
				pop.add(comp);
			}
			JComponent source = (JComponent) e.getSource();
			JComponent par = (JComponent) source.getParent();
			pop.show(par, source.getX(), source.getY() + source.getHeight());
		}
	}

	protected BPXYDData createCloneData(List<Map<String, Object>> datas)
	{
		BPXYDDataBase xydata = new BPXYDDataBase();
		xydata.setColumnNames(new String[] { "title", "content", "date", "url" });
		xydata.setColumnClasses(new Class[] { String.class, String.class, String.class, String.class });
		xydata.setColumnLabels(new String[] { "title", "content", "date", "url" });
		List<BPXData> xdatas = new ArrayList<BPXData>();
		for (Map<String, Object> data : datas)
		{
			String datestr = null;
			Long date = (Long) data.get("date");
			if (date != null)
				datestr = DateUtil.formatTime(date);
			BPXDataArray xdata = new BPXDataArray(new Object[] { data.get("title"), data.get("content"), datestr, data.get("url") });
			xdatas.add(xdata);
		}
		xydata.setDatas(xdatas);
		return xydata;
	}

	public static class BPTableFuncsWebNewsResult extends BPTableFuncsMapMethod
	{
		public List<Action> getActions(BPTable<Map<String, Object>> table, List<Map<String, Object>> datas, int[] rows, int r, int c)
		{
			List<Action> rc = new ArrayList<Action>();
			if (datas != null && datas.size() > 0)
			{
				Action actopen = BPAction.build("Open").callback((e) ->
				{
					open(datas);
				}).getAction();
				Action actview = BPAction.build("View").callback((e) ->
				{
					view(datas);
				}).getAction();
				rc.add(actview);
				rc.add(actopen);
			}
			return rc;
		}

		public void view(List<Map<String, Object>> datas)
		{
			Map<String, Object> data = datas.get(0);
			UIStd.textarea(ObjUtil.toString(data.get("content"), ""), ObjUtil.toString(data.get("title"), ""), false, true);
		}

		public void open(List<Map<String, Object>> datas)
		{
			for (Map<String, Object> data : datas)
			{
				try
				{
					String url = (String) data.get("url");
					if (url == null)
						return;
					URI uri = new URI(url);
					CommonUIOperations.openExternal(uri);
				}
				catch (URISyntaxException e)
				{
					UIStd.err(e);
				}
			}
		}
	}

	public final static class BPEditorFactoryWebNewsPanel implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatUnknown.FORMAT_NA };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			BPWebNewsPanel rc = new BPWebNewsPanel();
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
		}

		public String getName()
		{
			return "Web News Panel";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}
}
