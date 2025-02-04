package bp.ui.form;

import java.util.function.BiConsumer;

public class BPFormPanelFactoryWebCFs implements BPFormPanelFactory
{
	public void register(BiConsumer<String, Class<? extends BPFormPanel>> regfunc)
	{
		regfunc.accept("bp.project.BPResourceProjectWebSite", BPFormPanelWebSiteProject.class);
		regfunc.accept("bp.res.BPResourceWebSiteLink", BPFormPanelWebSiteLink.class);
		regfunc.accept("bp.task.BPTaskWebSiteOperation", BPFormPanelTaskWebSiteOperation.class);
		regfunc.accept("bp.task.BPTaskSendPing", BPFormPanelTaskSendPing.class);
	}
}
