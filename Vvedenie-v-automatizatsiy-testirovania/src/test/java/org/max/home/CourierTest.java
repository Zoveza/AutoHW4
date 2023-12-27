package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.max.home.CourierInfoEntity;
import org.max.home.AbstractTest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourierTest extends AbstractTest {

    @Test
    @Order(1)
    void getCourier() throws SQLException {
        //given
        String sql = "SELECT * FROM courier";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(CourierInfoEntity.class);
        //then
        Assertions.assertEquals(4, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"John, Rython", "Kate , Looran"})
    void getCourierById(String name, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM courier WHERE first_name='John, Rython' + name + 'Kate , Looran'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(3);
        }
        //then
        Assertions.assertEquals(name, nameString);
    }

    @Test
    @Order(3)
    void addCourier() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 5);
        entity.setDeliveryType("foot");
        entity.setPhoneNumber("+7 981 564 0284");
        entity.setLastName("Lee");
        entity.setFirstName("Ashley");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        CourierInfoEntity creditEntity = (CourierInfoEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(creditEntity);
        Assertions.assertEquals("foot", creditEntity.getDeliveryType());
    }

    @Test
    @Order(4)
    void deleteNewCourier() {
        //given
        Session session = getSession();
        final Query queryCourier = getSession()
                .createSQLQuery("SELECT * FROM courier WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        List<CourierInfoEntity> entityForDelete = queryCourier.getResultList();
        Assumptions.assumeFalse(entityForDelete.isEmpty());
        //when
        for (CourierInfoEntity courierInfoEntity: entityForDelete) {
            session.beginTransaction();
            session.delete(courierInfoEntity);
            session.getTransaction().commit();
        }
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM courier WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        List<CourierInfoEntity> entity = queryAfterDelete.getResultList();
        Assertions.assertEquals(0, entity.size());
    }

}