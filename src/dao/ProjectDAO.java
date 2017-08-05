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

@SuppressWarnings("deprecation")
public class ProjectDAO extends GenericDAO<ProjectEntity> {

    public Collection<ProjectEntity> getProjectsOfTeam(String teamName) {
        Collection<ProjectEntity> result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProjectEntity.class).add(Expression.eq("team", teamName));
        result = (ArrayList<ProjectEntity>) criteria.list();
        session.getTransaction().commit();
        return result;
    }
    
    public ProjectEntity getProject(String projectName, String teamName) {
        ProjectEntity result;
        TeamDAO teamDAO = new TeamDAO();
        TeamEntity team = teamDAO.getTeamByTeamName(teamName);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProjectEntity.class).add
                (Expression.eq("name", projectName)).add(Expression.eq
                ("team", team));
        result = (ProjectEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        return result;
    }

    public void removeByName(String projectName, String teamName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProjectEntity.class).add(Expression.eq("name", projectName));
        session.delete(criteria.uniqueResult());
        session.getTransaction().commit();
    }
    
}
