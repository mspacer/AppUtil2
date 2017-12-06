package ibs.util.relation;

import ibs.util.db.AbstractBeanFactory;
import ibs.util.db.AbstractBeanFactory.StringObj;
import ibs.util.relation.db.RelationDAO;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

public class RelationManager {
	private final RelationDAO relationDAO;
	private final String message;
	private final String etc;

	public RelationManager(AbstractBeanFactory gFactory, String message, String etc) {
		this.relationDAO = new RelationDAO(gFactory);
		this.message = message;
		this.etc = etc;
	}

	public boolean hasRelations(Object id) throws Exception {
		return relationDAO.findRelationsCount(id, null) > 0;
	}

	public boolean hasRelations(Object id, int limit, Connection con, StringBuffer output) throws Exception {
		int count = relationDAO.findRelationsCount(id, con);
		boolean result = count > 0;
		if(result && null != output) {
			if(null != message) {
				output.append(message.replaceAll("\\{0\\}", String.valueOf(count)));
			}
			Collection relations = relationDAO.findRelations(id, limit, con);
			Iterator iterator = relations.iterator();
			
			for (int i = 1; iterator.hasNext(); i++) {
				if(i > 1) {
					output.append(";\n");
				}
				StringObj next = (StringObj) iterator.next();
				output.append(i).append(") ").append(next.getValue());
			}
			if(count > limit) {
				if(null != etc) {
					output.append(etc);
				}
			} else {
				output.append('.');
			}
		}
		return result;
	}
}
