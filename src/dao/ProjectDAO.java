package dao;

import java.util.ArrayList;
import java.util.Collection;

import entity.ProjectEntity;
import entity.TeamEntity;
import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

/**
 * This class is a data access object for ProjectEntity entries in the database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class ProjectDAO extends GenericDAO<ProjectEntity> {

	/**
	 * Gets a {@see Collection} of the ProjectEntity objects of a team saved in
	 * the database.
	 *
	 * @param teamName The name of the team of which the projects should get
	 *                    loaded.
	 *
	 * @return The list of all projects of a team. If there are no projects a
	 * empty Collection will be returned.
	 */
    public Collection<ProjectEntity> getProjectsOfTeam(String teamName) {
        Collection<ProjectEntity> result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProjectEntity.class).add(Expression.eq("team", teamName));
        result = (ArrayList<ProjectEntity>) criteria.list();
        session.getTransaction().commit();
        return result;
    }

	/**
	 * Gets a single ProjectEntity object out of the database.
	 *
	 * @param projectName The name of the project.
	 * @param teamName The name of the team to which the project belongs.
	 *
	 * @return A ProjectEntity object or null if it does not exist in the
	 * database.
	 */
	public ProjectEntity getProject(String projectName, String teamName) {
        ProjectEntity result;
        TeamDAO teamDAO = new TeamDAO();
        TeamEntity team = teamDAO.getTeamByTeamName(teamName);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProjectEntity.class).add
                (Expression.eq("name", projectName))
				.add(Expression.eq("team", team));
        result = (ProjectEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        return result;
    }

	/**
	 * Deletes a ProjectEntity object in the database if it exists.
	 *
	 * @param projectName The name of the project.
	 * @param teamName The name of the team the project belongs to.
	 */
	public void removeByName(String projectName, String teamName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProjectEntity.class)
				.add(Expression.eq("name", projectName));
        session.delete(criteria.uniqueResult());
        session.getTransaction().commit();
    }
    
}
