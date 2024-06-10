package com.example.Progetto.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.example.Progetto.dao.DaoLibro;
import com.example.Progetto.models.Libro;

@Service
public class ServiceLibro extends GenericService <Long, Libro, DaoLibro>{


    

    @Override
    public Libro createEntity(Map<String, String> map) {
        Libro l = getContext().getBean(Libro.class, map);
        return l;
    }

    public Libro insert(Map<String, String> map){
        Libro l = createEntity(map);
        Long id = getDao().create(l);
        l.setId(id);
        return l;
    }
    
    public List<Libro> findAll() {
        return getDao().read().values().stream().map(x -> {
            return (Libro) x;
        }).toList();
    }

    public boolean update(Map<String, String> map) {
        Libro l = createEntity(map);
        getDao().update(l);
        return true;
    }
    public void associaLU(Long idLibro, Long idUtente) {
         getDao().aLibroUtente(idLibro, idUtente);
    }
    public void dissociaLU(Long idLibro, Long idUtente) {
        getDao().dLibroUtente(idLibro, idUtente);
    }
    public Libro findByTitolo(String titolo) {
        return getDao().readByTitolo(titolo);
    }
    public List<Libro> findByGenere(String genere) {
        return getDao().readByGenere(genere);
    }

    public List<Libro> byAnno() {
        return getDao().orderByAnno();
    }

    public List<Libro> byGenere() {
        return getDao().orderByGenere();
    }

    public List<Libro> byRatings() {
        return getDao().orderByRatings();
    }

    public List<Libro> readByIdUtente(Long id) {
        return getDao().readByUtente(id);
    }
    public List<Libro> readByAutore(Long id) {
        return getDao().readLibriAutore(id);
    }
    //lista delle recensioni di un determinato libro
    public List<Map<String,String>> readRecensioni(Long id) {
        return getDao().readRecensione(id);
    }
    public void creaRecensione(Map<String,String> map,Long idUtente) {
        Long idLibro= Long.parseLong(map.get("id"));
        String recensione= map.get("recensione");
        if(map.get("recensione")!=null){
            getDao().creaRecensione(idLibro, idUtente, recensione);
        }
    }

    public void aggiungiRecensione(Map<String,String> map,Long idUtente) {

        //prendimi dalla mappa il value della primary key id
    Long idLibro= Long.parseLong(map.get("id"));
    String recensione= map.get("recensione");
if(map.get("recensione")!=null){
       System.out.println("id libro: "+idLibro+" id utente: "+idUtente+" recensione: "+recensione);

        getDao().addRecensione(idLibro, idUtente, recensione);
}
    }
    public Libro findById(Long id) {
        return getDao().readById(id);
    }
            

   public void updatePagine( Long idLibro, Long idUtente, int pagineLette){
    System.out.println(idLibro + " " + idUtente + " " + pagineLette);
     getDao().updatePagineLette( idLibro, idUtente, pagineLette);
   }

   public int readPagineLette(Long idLibro, Long idUtente){
       return getDao().readPagineLette(idLibro, idUtente);
   }

   public boolean delete(Long id){
       getDao().delete(id);
       return true;
   }

    public boolean readAssociazione(Long idLibro, Long idUtente){
         return getDao().readAssociazione(idLibro, idUtente);
    }
    public void vota(Long idLibro, double voto){
  

        getDao().vota(idLibro,voto);
    }

   public double readRatingPersonale(Long idLibro, Long idUtente){
       return getDao().readRatingPersonale(idLibro, idUtente);
   }

   public void addRatingPersonale(Long idLibro, Long idUtente, double voto){
       getDao().addRatingPersonale(idLibro, idUtente, voto);
   }
   public int numeroVotazioni(double idLibro){
         return getDao().numeroVotazioni(idLibro);
   }
   public double sommaVotazioni(double idLibro){
       return getDao().sommaVotazioni(idLibro);
   }
   public double readVoti(Long idLibro){
       return getDao().readVoti(idLibro);
   }

   public void deleteUtenteWhenAssocia(Long idLibro, Long idUtente){
       getDao().deleteUtenteWhenAssocia(idLibro, idUtente);
   }
   public String readRecensione(Long idLibro, Long idUtente){
       return getDao().readRecensione(idLibro, idUtente);
   }



}
