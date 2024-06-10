package com.example.Progetto.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.example.Progetto.models.Autore;
import com.example.Progetto.models.Libro;
import com.example.Progetto.services.ServiceAutore;
import com.example.Progetto.services.ServiceLibro;

import com.example.Progetto.services.ServiceUtente;
import jakarta.servlet.http.HttpSession;
import lombok.Data;


@Controller
@RequestMapping("/api/libro")
@Data
public class LibroController {
    @Autowired
    private  ServiceLibro serviceLibro;
    @Autowired
    private ServiceAutore serviceAutore;
    @Autowired
    private ServiceUtente serviceUtente;

   


    //htpps://localhost:8080/libro/all
    @GetMapping("/all")
    public String all(Model model,HttpSession session){
     List<Libro> ris = serviceLibro.findAll();      
  

       ris=controlloPresenza(ris, session);

    

        model.addAttribute("libri", ris);
        return "archivioCompleto.html";
    }
    private List<Libro> controlloPresenza(List<Libro> ris,HttpSession session){
        Long idUtente = (Long) session.getAttribute("idUtente");
        List<Libro> ris2 = serviceLibro.readByIdUtente(idUtente);
        for(Libro l:ris){
            for(Libro l2:ris2){
                if(l.getId()==l2.getId()){
                    l.setTitolo(l.getTitolo()+" ");
                }
            }
        }
        return ris;
    }

    @GetMapping("libroById")
    public String libroById(@RequestParam(name="idLibro", defaultValue = "0") Long id,Model model,HttpSession session){
        Libro l = serviceLibro.findById(id);
        List<Libro> ris =new ArrayList<>();
        ris.add(l);
        ris=controlloPresenza(ris, session);
      model.addAttribute("libri", ris);
        return "archivioCompleto.html";
    }

    private static double votoX;
    private static List<Double> voti=new ArrayList<>();
    @PostMapping("/votoo")
 public String voto(@RequestParam(name="idLibro", defaultValue = "0") Long id, 
 @RequestParam(name="rating", defaultValue = "0") double voto,@RequestParam(name="votoVecchio", defaultValue = "0") double votoVecchio,Model model,HttpSession session){
 Long idUtente = (Long) session.getAttribute("idUtente");


 serviceLibro.addRatingPersonale(id,idUtente, voto);


 double valoreIniziale=serviceLibro.readVoti(id);
 double votoTetto=serviceLibro.numeroVotazioni(id);


if(votoTetto==1&&valoreIniziale!=0)
{
    double votino=(1+serviceLibro.numeroVotazioni(id))*5;
    double sommaVotazioni=serviceLibro.sommaVotazioni(id)+valoreIniziale;
    double votazioneBella=(sommaVotazioni/votino)*5;
      serviceLibro.vota(id, votazioneBella);
      votoX=votoVecchio;
      voti.add(votoVecchio);
      

 
}else
{


    double votino=serviceLibro.numeroVotazioni(id)*5;
    double sommaVotazioni=serviceLibro.sommaVotazioni(id);
    double votazioneBella=(sommaVotazioni/votino)*5;

      serviceLibro.vota(id, votazioneBella);
}
    
        return "redirect:/api/libro/byId?idLibro="+id;
    }
    @GetMapping("/eliminaVoto")
    public String eliminaVoto(@RequestParam(name="idLibro", defaultValue = "0") Long id,Model model,HttpSession session,@RequestParam(name="votoVecchio", defaultValue = "0") double votoVecchio
   ){
        Long idUtente = (Long) session.getAttribute("idUtente");
     
        serviceLibro.addRatingPersonale(id,idUtente,-1);
      

        double valoreIniziale=serviceLibro.readVoti(id);
        double votoTetto=serviceLibro.numeroVotazioni(id);
       
       if(votoTetto==0&&valoreIniziale!=0)
       {
             serviceLibro.vota(id, voti.get(0));  
       }else
       {
       
       
           double votino=serviceLibro.numeroVotazioni(id)*5;
           double sommaVotazioni=serviceLibro.sommaVotazioni(id);
           double votazioneBella=(sommaVotazioni/votino)*5;
             serviceLibro.vota(id, votazioneBella);
       }
           
     
        
        return "redirect:/api/libro/byId?idLibro="+id;
    }

    

    @GetMapping("byId")
    public String dettagliLibro(@RequestParam(name="idLibro", defaultValue = "0") Long id,Model model,HttpSession session){
        Libro l = serviceLibro.findById(id);
        List<Map<String,String>> ris =   serviceLibro.readRecensioni(id);
        Long idUtente = (Long) session.getAttribute("idUtente");
        int paginelette = serviceLibro.readPagineLette(id, idUtente);
        String username = (String) session.getAttribute("username");
        double votoUtente=serviceLibro.readRatingPersonale(id, idUtente);
//se esiste un'associazione tra l'id del libro e l'id dell'utente allora la variabile associazione è true
        boolean associazione=serviceLibro.readAssociazione(id, idUtente);
        double votoTetto=serviceLibro.numeroVotazioni(id);
        if (votoTetto==1) {
            model.addAttribute("votoTetto", "true");
        } else {
            model.addAttribute("votoTetto", "false");
            
        }
     
       //se ris contiene nella primary key recensione una recensione allora la variabile avereRec è true
       if(id==null)
        model.addAttribute("error", "id non valido!");

        int index = -1;
        for(int i = 0; i < ris.size(); i++){
            Map<String,String> m = ris.get(i);
            if(m.get("recensione")!=null && m.get("username").equals(username)){
                model.addAttribute("avereRec", "true");
                index = i;
                break;
            }
            else {
                model.addAttribute("avereRec", "false");
            }
        }
        
        if(index != -1){
            Map<String,String> m = ris.remove(index);
            ris.add(0, m);
        }

        

       //id del libro
       model.addAttribute("ratingPersonale", votoUtente);
        model.addAttribute("idLibro", id);
       model.addAttribute("paginelette", paginelette);
      model.addAttribute("associazione", associazione);
      model.addAttribute("contRec", ris.size());
  
        model.addAttribute("recensioni",ris);
        model.addAttribute("libro", l);
        return "dettaglioLibro.html";
    }
    @GetMapping("/recenti")
    public String orderBy(Model model,HttpSession session){
        List<Libro> ris = serviceLibro.byAnno();
        ris=controlloPresenza(ris, session);
  
        model.addAttribute("libri", ris);
        return "libriOrderUscita.html";
       
    }

    @GetMapping("/ratings")
    public String orderByRatings(Model model,HttpSession session){
        List<Libro> ris = serviceLibro.byRatings();
        ris=controlloPresenza(ris, session);
  
        model.addAttribute("libri", ris);
        return "libriOrderRatings.html";
       
    }

    @GetMapping("/genere")
    public String orderByGenere(Model model,HttpSession session){
        List<Libro> ris = serviceLibro.byGenere();
        ris=controlloPresenza(ris, session);
  
        model.addAttribute("libri", ris);
        return "libriOrderGenere.html";
       
    }
    @GetMapping("/byGenere")
    public String orderByGenere(@RequestParam(name="genere",defaultValue = "") String genere,Model model,HttpSession session){
        List<Libro> ris = serviceLibro.findByGenere(genere);
  ris=controlloPresenza(ris, session);
        model.addAttribute("libri", ris);
        return "archivioCompleto.html";
       
    }
 
//aggiungere recensione ad un libro in base all'utente in sessione

@PostMapping("/creaRecensione")
public String creaRecensione(@RequestParam Map<String,String> params,Model model,HttpSession session){
    Long idUtente = (Long) session.getAttribute("idUtente");
    serviceLibro.creaRecensione(params, idUtente);
    //ottieni l'id del da params
    Long id = Long.parseLong(params.get("id"));
    //ritorna alla pagina dei libriutenti
    return "redirect:/api/libro/byId?idLibro="+id;
}
@PostMapping("/aggiungiRecensione")
public String aggiungiRecensione(@RequestParam Map<String,String> params,Model model,HttpSession session){



    
    Long idUtente = (Long) session.getAttribute("idUtente");
   
    serviceLibro.aggiungiRecensione(params, idUtente);
    //ottieni l'id del da params
    Long id = Long.parseLong(params.get("id"));
    //ritorna alla pagina dei libriutenti
    return "redirect:/api/libro/byId?idLibro="+id;


}

    @GetMapping("/byAutore")
    public String orderByAutore(@RequestParam(name="idAutore", defaultValue = "0") Long id,Model model){
        List<Libro> ris = serviceLibro.readByAutore(id);
  
        model.addAttribute("libri", ris);
        return "archivioCompleto.html";
}
@PostMapping("challenge")
public String libriChallenge(@RequestParam(name="libriChallenge", defaultValue = "0") int idLibroCh,Model model,HttpSession session){
    //id utenete in sessione
    Long idUtente = (Long) session.getAttribute("idUtente");
    serviceUtente.addLibroChallenge(idUtente, idLibroCh);
    return "redirect:/homeUtente";
}

    @GetMapping("/libriUtente")
    public String libriUtente(Model model, HttpSession session,@RequestParam(name="count",  defaultValue="0") int conteggio){
        Long idUtente = (Long) session.getAttribute("idUtente");
        List<Libro> ris = serviceLibro.readByIdUtente(idUtente);
        if(ris.isEmpty())
        {model.addAttribute("vuoto", "true");
            return "libriUtente.html";
        }
       
        model.addAttribute("libri", ris);
        model.addAttribute("count", conteggio);
        return "libriUtente.html";
    }
    @GetMapping("/genereUtente")
    public String genereUtente(Model model,HttpSession session){
        Long idUtente = (Long) session.getAttribute("idUtente");
        List<Libro> ris = serviceLibro.readByIdUtente(idUtente);

        Map.Entry<String, Long> mostFrequentGenreEntry = ris.stream()
        .collect(Collectors.groupingBy(Libro::getGenere, Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .orElse(null);
    
    String mostFrequentGenre = null;
    Long count = null;
    if (mostFrequentGenreEntry != null) {
        mostFrequentGenre = mostFrequentGenreEntry.getKey();
        count = mostFrequentGenreEntry.getValue();
    }
    if(count==null)
    count=0L;
     
    
        List<Libro> ris2 = new ArrayList<>();
        if(count==0||count==0L&&ris.isEmpty())
       ris2= serviceLibro.byGenere();
        else
        ris2=serviceLibro.findByGenere(mostFrequentGenre);

        ris2=controlloPresenza(ris2, session);


        model.addAttribute("libri", ris2);
        return "archivioCompleto.html";
    }


    @GetMapping("/aggiungiLibro")
    public String aggiungiLibro(Model model,HttpSession session,@RequestParam(name="idLibro", defaultValue = "0") Long id){
        Long idUtente = (Long) session.getAttribute("idUtente");
        List<Libro> ris = serviceLibro.readByIdUtente(idUtente);
        for(Libro l:ris){
            if(l.getId()==id){
                model.addAttribute("error", "Libro già presente");
                return "mainError.html";
            }
        }
        serviceLibro.associaLU(id, idUtente);
        //libro in base all'id
        Libro l = serviceLibro.findById(id);
        model.addAttribute("inserito", "Libro inserito!!");
        model.addAttribute("libri", l);
       return "libroInserito.html";
     
     
    
       
    }
    
    @PostMapping("/eliminaDaLista")
    public String eliminaDaLista(Model model,HttpSession session,@RequestParam(name="idLibro", defaultValue = "0") Long id,
  @RequestParam(name="conto", defaultValue = "0")int count,RedirectAttributes redirectAttributes){
        Long idUtente = (Long) session.getAttribute("idUtente");

      
        int conteggio=count; 
        conteggio++;
        
       
     
        redirectAttributes.addAttribute("count", conteggio);


     

     
        serviceLibro.dissociaLU(id, idUtente);
       return "redirect:/api/libro/libriUtente";
    }



    @PostMapping("/search")
    public String search(@RequestBody String titolo,Model model,HttpSession session){
     
        String titoloOk=titolo.substring(6,titolo.length());
        if(titoloOk.contains("+") )
            titoloOk=titoloOk.replace("+"," ");

            if(titoloOk.contains("%27") )
            titoloOk=titoloOk.replace("%27","'");
        Libro l = serviceLibro.findByTitolo(titoloOk);
        Autore a = serviceAutore.findByCognome(titoloOk);
        if (l==null) {
       model.addAttribute("autore", a);
        return "archivioAutori.html";
        } else {
            List<Libro> ris =new ArrayList<>();
            ris.add(l);
            ris=controlloPresenza(ris, session);
          model.addAttribute("libri", ris);
           
            return "archivioCompleto.html";

        }
    

    }
    
    @PostMapping("/add")
    public String add(@RequestParam Map<String,String> map,Model model,RedirectAttributes redirectAttributes){
      
        List<Autore> autori = serviceAutore.findAll();

        //unire il nome e cognome di ogni autore della lista autori
        for (Autore autore : autori) {
            String nomeCognome=autore.getNome()+" "+autore.getCognome();
   
            //se il nome e cognome dell'autore è uguale al nome e cognome dell'autore del libro
            if(nomeCognome.equalsIgnoreCase(map.get("autore"))){
                //la mappa con chiave id_autore prendera il valore dell'id dell'autore
                map.put("id_autore", String.valueOf(autore.getId()));
          
            }
          
                   
        
    }
    //se ò'autore non è trovato nella lista autori allora da errore
    if(!map.containsKey("id_autore")){
        model.addAttribute("error", "Autore non trovato");
        return "mainError.html";

    }
        //cercare l'id dell'autore che ha come+cognome come autore del libro
serviceLibro.insert(map);
     
        return "redirect:/api/libro/all";
    }
    @PostMapping("/update")
    public String update(@RequestParam Map<String,String> map,Model model){
        List<Autore> autori = serviceAutore.findAll();

        //unire il nome e cognome di ogni autore della lista autori
        for (Autore autore : autori) {
            String nomeCognome=autore.getNome()+" "+autore.getCognome();
   
            //se il nome e cognome dell'autore è uguale al nome e cognome dell'autore del libro
            if(nomeCognome.equalsIgnoreCase(map.get("autore"))){
                //la mappa con chiave id_autore prendera il valore dell'id dell'autore
                map.put("id_autore", String.valueOf(autore.getId()));
          
            }            
        
    }
    //se ò'autore non è trovato nella lista autori allora da errore
    if(!map.containsKey("id_autore")){
        model.addAttribute("error", "Autore non trovato");
        return "mainError.html";
      

    }
        serviceLibro.update(map);
        return "redirect:/api/libro/all";
    }




   @PostMapping("/updatePagineLette")
    public String updatePagineLette(HttpSession session, @RequestParam(name="id", defaultValue = "0") Long idLibro, 
    @RequestParam(name= "nPagineLette", defaultValue = "0") int pagineLette,Model model) {
        
        Long idUtente= (Long) session.getAttribute("idUtente");
        try {
        // Chiamata al servizio per aggiornare le pagine lette
        
        serviceLibro.updatePagine(idLibro, idUtente, pagineLette);
        

            return "redirect:/api/libro/byId?idLibro="+idLibro;
        } catch (Exception e) {
            model.addAttribute("error", "Errore nell'aggiornamento delle pagine lette");
            return "mainError.html";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam(name="idLibro", defaultValue = "0") Long id,Model model){
        serviceLibro.delete(id);
        return "redirect:/api/libro/all";
    }
}
