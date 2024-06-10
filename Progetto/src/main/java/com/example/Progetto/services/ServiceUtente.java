package com.example.Progetto.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.example.Progetto.dao.DaoUtente;

import com.example.Progetto.models.Entity;
import com.example.Progetto.models.Utente;

@Service
public class ServiceUtente{
        //AGGIUNTO SERVICEUTENTE
        @Autowired
        private DaoUtente daoUtente;
    
        @Autowired
        private ApplicationContext applicationContext;
    
        public void create(Map<String,String> params) {
            params.put("ruolo", "user");       
            Utente u = applicationContext.getBean(Utente.class, params);

            daoUtente.create(u);
        }
    
        
    
        public Utente readById(Long id) {
            Entity e = daoUtente.readById(id);
            if(e instanceof Utente)
                return (Utente)e;
            return null;
        }
    
        public void update(Map<String,String> params) {
            Utente u = applicationContext.getBean("utente",Utente.class);
            daoUtente.update(u);
        }
    public void delete(Long id) {
            daoUtente.delete(id);
        }
    
        public boolean readByUserName(String username){
            return daoUtente.userExists(username);
        }
    
        public Utente readByUsernameAndPassword(String username, String password) {
            Map<String,String> map= daoUtente.autentica(username, password);
            if (map!=null) {
                System.out.println(map);
                return applicationContext.getBean(Utente.class,map);
            }
            
            return null;
    
        }
        


        public void updateUsername(Long id, String newUsername) {
            daoUtente.updateUsername(id, newUsername);
        }
        public void updatePassword(Long id, String newPassword) {
            daoUtente.updatePassword(id, newPassword);
        }


        public boolean readByEmail(String email) {
            return daoUtente.userExistsByEmail(email);
        }
        public int readLibriChallenge(Long idUtente) {
            return daoUtente.readLibriChallenge(idUtente);
        }
       public void addLibroChallenge(Long idUtente,int libCh)
       {
              daoUtente.addLibroChallenge(idUtente, libCh);
       }
         public void addLibroLetti(Long idUtente,int libLetti)
         {
                  daoUtente.addLibroLetti(idUtente, libLetti);
         }
            public int readLibriLetti(Long idUtente)
            {
                return daoUtente.readLibriLetti(idUtente);
            }

    }
