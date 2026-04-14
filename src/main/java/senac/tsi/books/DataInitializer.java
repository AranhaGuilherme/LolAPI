package senac.tsi.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import senac.tsi.books.entities.*;
import senac.tsi.books.repositories.*;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private CoachRepository coachRepository;
    @Autowired private ChampionRepository championRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private MatchGameRepository matchGameRepository;

    @Override
    public void run(String... args) {
        if (coachRepository.count() > 0) return;

        // ── COACHES ──────────────────────────────────────────────────────────
        Coach kkoma   = coachRepository.save(new Coach("Kkoma",    12));
        Coach zefa    = coachRepository.save(new Coach("Zefa",      8));
        Coach deilor  = coachRepository.save(new Coach("Deilor",   10));
        Coach ssong   = coachRepository.save(new Coach("Ssong",     9));
        Coach mithy   = coachRepository.save(new Coach("Mithy",     6));

        // ── CAMPEÕES ─────────────────────────────────────────────────────────
        Champion orianna  = championRepository.save(new Champion("Orianna",  Role.MID));
        Champion leeSin   = championRepository.save(new Champion("Lee Sin",  Role.JUNGLE));
        Champion thresh   = championRepository.save(new Champion("Thresh",   Role.SUPPORT));
        Champion jinx     = championRepository.save(new Champion("Jinx",     Role.ADC));
        Champion darius   = championRepository.save(new Champion("Darius",   Role.TOP));

        // ── TIMES ────────────────────────────────────────────────────────────
        Team t1 = new Team("T1",          "Korea");         t1.setCoach(kkoma);
        Team g2 = new Team("G2 Esports",  "Europe");        g2.setCoach(zefa);
        Team fnatic = new Team("Fnatic",  "Europe");        fnatic.setCoach(deilor);
        Team cloud9 = new Team("Cloud9",  "North America"); cloud9.setCoach(ssong);
        Team liquid = new Team("Team Liquid", "North America"); liquid.setCoach(mithy);

        t1     = teamRepository.save(t1);
        g2     = teamRepository.save(g2);
        fnatic = teamRepository.save(fnatic);
        cloud9 = teamRepository.save(cloud9);
        liquid = teamRepository.save(liquid);

        // ── JOGADORES ────────────────────────────────────────────────────────
        Player faker   = new Player("Lee Sang-hyeok",  "Faker",   Role.MID);     faker.setTeam(t1);
        Player caps    = new Player("Rasmus Winther",  "Caps",    Role.MID);     caps.setTeam(g2);
        Player blaber  = new Player("Robert Huang",    "Blaber",  Role.JUNGLE);  blaber.setTeam(cloud9);
        Player coreJJ  = new Player("Jo Yong-in",      "CoreJJ",  Role.SUPPORT); coreJJ.setTeam(liquid);
        Player rekkles = new Player("Martin Larsson",  "Rekkles", Role.ADC);     rekkles.setTeam(fnatic);

        faker   = playerRepository.save(faker);
        caps    = playerRepository.save(caps);
        blaber  = playerRepository.save(blaber);
        coreJJ  = playerRepository.save(coreJJ);
        rekkles = playerRepository.save(rekkles);

        // ── PARTIDAS ─────────────────────────────────────────────────────────
        MatchGame m1 = new MatchGame("32:15");
        m1.setTimeA(t1);     m1.setTimeB(g2);     m1.setVencedor(t1);
        m1.setPlayers(List.of(faker, caps));
        m1.setChampions(List.of(orianna, thresh));
        matchGameRepository.save(m1);

        MatchGame m2 = new MatchGame("45:30");
        m2.setTimeA(cloud9); m2.setTimeB(liquid); m2.setVencedor(liquid);
        m2.setPlayers(List.of(blaber, coreJJ));
        m2.setChampions(List.of(leeSin, thresh));
        matchGameRepository.save(m2);

        MatchGame m3 = new MatchGame("28:45");
        m3.setTimeA(t1);     m3.setTimeB(fnatic); m3.setVencedor(t1);
        m3.setPlayers(List.of(faker, rekkles));
        m3.setChampions(List.of(orianna, jinx));
        matchGameRepository.save(m3);

        MatchGame m4 = new MatchGame("38:20");
        m4.setTimeA(g2);     m4.setTimeB(cloud9); m4.setVencedor(g2);
        m4.setPlayers(List.of(caps, blaber));
        m4.setChampions(List.of(orianna, leeSin));
        matchGameRepository.save(m4);

        MatchGame m5 = new MatchGame("51:10");
        m5.setTimeA(fnatic); m5.setTimeB(liquid); m5.setVencedor(fnatic);
        m5.setPlayers(List.of(rekkles, coreJJ));
        m5.setChampions(List.of(jinx, darius));
        matchGameRepository.save(m5);
    }
}
