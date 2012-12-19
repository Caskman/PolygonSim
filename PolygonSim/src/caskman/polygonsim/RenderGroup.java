package caskman.polygonsim;

import java.util.List;

public class RenderGroup {

	private long creationTime;
	private List<RenderObject> renderList;
	
	public RenderGroup(long createTime,List<RenderObject> renderList) {
		this.creationTime = createTime;
		this.renderList = renderList;
	}
	
	public long getCreationTime() {
		return creationTime;
	}

	public List<RenderObject> getRenderObjectList() {
		return renderList;
	}
}
