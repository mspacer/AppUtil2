package ibs.util.relation.db;

import ibs.util.db.AbstractBeanFactory;
import ibs.util.db.AbstractBeanFactory.StringObj;

import java.sql.Connection;
import java.util.Collection;

public class RelationDAO {
	private final AbstractBeanFactory gFactory;

	public RelationDAO(AbstractBeanFactory gFactory) {
		this.gFactory = gFactory;
	}

	public int findRelationsCount(Object id, Connection con) throws Exception {
		Integer result = gFactory.queryInteger("select count(1) value from app_relations where id=?", new Object[]{id}, con);
		return null == result ? 0 : result.intValue();
	}

	public Collection findRelations(Object id, int limit, Connection con) throws Exception {
		return gFactory.findBeans(
			"select name value from (select name, row_number() over(order by ordering) rn from app_relations where id=?) where rn <= ? order by rn"
			, new Object[]{id, new Integer(limit)}, StringObj.class, con);
	}
}
