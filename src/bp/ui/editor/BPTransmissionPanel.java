package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.env.BPEnvManager;
import bp.env.BPEnvTransmission;
import bp.event.BPEventCoreUI;
import bp.format.BPFormat;
import bp.format.BPFormatUnknown;
import bp.res.BPResource;
import bp.task.BPTask;
import bp.task.BPTaskFactory;
import bp.task.BPTaskTransmission;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogForm;
import bp.ui.dialog.BPDialogNewTask;
import bp.ui.form.BPFormManager;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPProgressBar;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.table.BPTableFuncsTask;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.FileUtil;
import bp.util.NumberUtil;
import bp.util.ObjUtil;

public class BPTransmissionPanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 822959437181535501L;

	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;

	protected BPTable<BPTask<?>> m_tabtasks;
	protected BPTableModel<BPTask<?>> m_model;
	protected BPToolBarSQ m_toolbar;

	protected Consumer<BPEventCoreUI> m_statushandler;
	protected Consumer<BPEventCoreUI> m_changedhandler;

	protected Color m_pgselcolor;

	public BPTransmissionPanel()
	{
		initUIComponents();
		initDatas();
		initEvents();
	}

	protected void initEvents()
	{
		m_statushandler = this::onTaskStatusChanged;
		m_changedhandler = this::onTaskChanged;
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKSTATUS, m_statushandler);
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASK, m_changedhandler);
		setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new UIUtil.BPDropTargetListener(null, null, null, null, this::onDrop)));
	}

	protected void initUIComponents()
	{
		m_tabtasks = new BPTable<BPTask<?>>(new BPTableFuncsTask());
		m_toolbar = new BPToolBarSQ(true);
		m_toolbar.setBarHeight(22);
		m_model = m_tabtasks.getBPTableModel();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(m_tabtasks);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_tabtasks.setTableFont();
		m_pgselcolor = UIManager.getColor("Table.selectionBackground");

		BPToolVIconButton btnadd = new BPToolVIconButton(BPAction.build("Add").callback(this::onAdd).tooltip("Add Task").vIcon(BPIconResV.ADD()).getAction());
		BPToolVIconButton btnaddmagnet = new BPToolVIconButton(BPAction.build("Add Magnet").callback(this::onAddMagnet).tooltip("Add Magnet").vIcon(BPIconResV.ADD()).getAction());
		BPToolVIconButton btndel = new BPToolVIconButton(BPAction.build("Del").callback(this::onDel).tooltip("Remove Task").acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)).vIcon(BPIconResV.DEL()).getAction(), this);
		BPToolVIconButton btnstart = new BPToolVIconButton(BPAction.build("Start").callback(this::onStart).tooltip("Start Task").acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).vIcon(BPIconResV.START()).getAction());
		BPToolVIconButton btnstop = new BPToolVIconButton(BPAction.build("Stop").callback(this::onStop).tooltip("Stop Task").vIcon(BPIconResV.STOP()).getAction());
		BPToolVIconButton btnedit = new BPToolVIconButton(BPAction.build("Edit").callback(this::onEdit).tooltip("Edit Task").vIcon(BPIconResV.EDIT()).getAction());
		int btnsize = (int) (16f * UIConfigs.UI_SCALE());
		setupButtons(btnsize, btnadd, btndel, btnstart, btnstop, btnedit);

		m_tabtasks.setModel(m_model);
		m_tabtasks.setDefaultRenderer(Float.class, new BPTable.BPTableRendererReplace(this::getCellComponent));
		m_toolbar.add(Box.createRigidArea(new Dimension(4, 4)));
		m_toolbar.add(btnadd);
		m_toolbar.add(btnaddmagnet);
		m_toolbar.add(btndel);
		m_toolbar.add(btnstart);
		m_toolbar.add(btnstop);
		m_toolbar.add(btnedit);

		setLayout(new BorderLayout());
		m_toolbar.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		add(sp, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
	}

	protected void setupButtons(int btnsize, BPToolVIconButton... btns)
	{
		for (BPToolVIconButton btn : btns)
		{
			btn.setButtonSize(btnsize);
		}
	}

	protected void initDatas()
	{
		List<BPTask<?>> datas = new ArrayList<BPTask<?>>();
		datas.addAll(BPCore.getWorkspaceContext().getTaskManager().listTasks(BPTaskTransmission.CATEGORY_TRANSMISSION));
		m_model.setDatas(datas);
		m_model.fireTableDataChanged();
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

	public void clearResource()
	{
		setDropTarget(null);
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
		return "Transmissions";
	}

	protected Component getCellComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
		Component rc = null;
		if (value != null && col == 2)
		{
			BPProgressBar pbar = new BPProgressBar();
			pbar.setMaximum(1000);
			pbar.setFont(table.getFont());
			pbar.setSelectedBackgroundColor(m_pgselcolor);
			pbar.setSelectedBackground(isSelected);
			float v = ((Number) value).floatValue();
			int v2 = (int) Math.floor(v * 1000f);
			String pstr = m_model.getRow(row).getProgressText();
			if (pstr == null)
			{
				String vstr = NumberUtil.formatPercent(v);
				pbar.setString(vstr);
			}
			else
			{
				pbar.setString(pstr);
			}
			pbar.setValue(v2);
			pbar.setStringPainted(true);
			rc = pbar;
		}
		return rc;
	}

	protected void onDrop(DropTargetDropEvent e)
	{
		e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable t = e.getTransferable();
		try
		{
			if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				List<?> fs = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
				for (Object fobj : fs)
				{
					File f = (File) fobj;
					addFile(f.getAbsolutePath());
				}
			}
		}
		catch (UnsupportedFlavorException | IOException e1)
		{
			UIStd.err(e1);
		}
	}

	protected void addFile(String filename)
	{
		String ext = FileUtil.getExt(filename);
		if (ext == null)
			return;
		if (FileUtil.isIgnoreSensitive())
			ext = ext.toLowerCase();

		ServiceLoader<BPTaskFactory> facs = ClassUtil.getExtensionServices(BPTaskFactory.class);
		BPTaskFactory tfac = null;
		for (BPTaskFactory fac : facs)
		{
			String[] exts = fac.getExts();
			if (exts != null)
			{
				for (String e : exts)
				{
					if (e.equals(ext))
					{
						tfac = fac;
						break;
					}
				}
			}
		}
		if (tfac != null)
		{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("_CREATE_FROM_FILE", filename);
			BPTask<?> task = tfac.create(params);
			if (task != null)
			{
				BPCore.addTask(task);
			}
		}
	}

	protected void onAddMagnet(ActionEvent event)
	{
		String magnet = UIStd.input("", "Magnet Link:", "Input");
		if (magnet != null)
		{
			magnet = magnet.trim();
			if (magnet.length() > 0)
			{
				String ext = "[Magnet URI]";
				ServiceLoader<BPTaskFactory> facs = ClassUtil.getExtensionServices(BPTaskFactory.class);
				BPTaskFactory tfac = null;
				for (BPTaskFactory fac : facs)
				{
					String[] exts = fac.getExts();
					if (exts != null)
					{
						for (String e : exts)
						{
							if (e.equals(ext))
							{
								tfac = fac;
								break;
							}
						}
					}
				}
				if (tfac != null)
				{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("_CREATE_FROM_TEXT", magnet);
					BPTask<?> task = tfac.create(params);
					if (task != null)
					{
						BPCore.addTask(task);
					}
				}
			}
		}
	}

	protected void onAdd(ActionEvent e)
	{
		BPDialogNewTask dlg = new BPDialogNewTask();
		dlg.setFilter((fac) ->
		{
			String cat = fac.getCategory();
			return BPTaskTransmission.CATEGORY_TRANSMISSION.equals(cat);
		});
		dlg.setInitFormCallback((form) ->
		{
			form.showData(ObjUtil.makeMap("workdir", BPEnvManager.getEnvValue(BPEnvTransmission.ENV_NAME_TRANSMISSION, BPEnvTransmission.ENVKEY_WORKDIR)));
		});
		dlg.setVisible(true);
		BPTask<?> task = dlg.getTask();
		if (task != null)
		{
			BPCore.addTask(task);
		}
	}

	protected void onDel(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		if (tasks != null && tasks.size() > 0 && UIStd.confirm(this.getTopLevelAncestor(), "BlockP - Transmissions", "Delete Task?"))
		{
			for (BPTask<?> task : tasks)
			{
				task.stop();
				BPCore.removeTask(task);
			}
		}
	}

	protected void onStop(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		for (BPTask<?> task : tasks)
		{
			if (task.isRunning())
				task.stop();
		}
	}

	protected void onStart(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		for (BPTask<?> task : tasks)
		{
			if (!task.isRunning())
				task.start();
		}
	}

	protected void onEdit(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		if (tasks.size() > 0)
		{
			BPTask<?> task = tasks.get(0);
			boolean isrun = task.isRunning();
			BPDialogForm dlg = new BPDialogForm();
			dlg.setEditable(!isrun);
			Class<?> c = ClassUtil.tryLoopSuperClass((cls) ->
			{
				if (BPFormManager.containsKey(cls.getName()))
					return cls;
				return null;
			}, task.getClass(), BPTask.class);
			dlg.setup(c == null ? task.getClass().getName() : c.getName(), task);
			dlg.setTitle("Task:" + task.getName());
			dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(700, 600)));
			dlg.pack();
			dlg.setLocationRelativeTo(null);
			dlg.setVisible(true);
			if (!isrun)
			{
				Map<String, Object> formdata = dlg.getFormData();
				if (formdata != null)
				{
					task.setMappedData(formdata);
					BPCore.saveTasks();
					m_model.fireTableDataChanged();
				}
			}
		}
	}

	protected void onTaskStatusChanged(BPEventCoreUI event)
	{
		BPTask<?> task = (BPTask<?>) event.datas[0];
		List<BPTask<?>> tasks = m_model.getDatas();
		int i = tasks.indexOf(task);
		if (i > -1)
		{
			m_model.fireTableCellUpdated(m_model.getDatas().indexOf(task), 1);
			m_model.fireTableCellUpdated(m_model.getDatas().indexOf(task), 2);
		}
	}

	protected void onTaskChanged(BPEventCoreUI event)
	{
		initDatas();
	}

	public final static class BPEditorFactoryTransmissionPanel implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatUnknown.FORMAT_NA };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			BPTransmissionPanel rc = new BPTransmissionPanel();
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
		}

		public String getName()
		{
			return "Transmission Panel";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}
}