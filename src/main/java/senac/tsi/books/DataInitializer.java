package senac.tsi.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import senac.tsi.books.entities.Champion;
import senac.tsi.books.entities.Coach;
import senac.tsi.books.entities.MatchGame;
import senac.tsi.books.entities.Player;
import senac.tsi.books.entities.Role;
import senac.tsi.books.entities.Team;
import senac.tsi.books.repositories.ChampionRepository;
import senac.tsi.books.repositories.CoachRepository;
import senac.tsi.books.repositories.MatchGameRepository;
import senac.tsi.books.repositories.PlayerRepository;
import senac.tsi.books.repositories.TeamRepository;

import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private CoachRepository coachRepository;
    @Autowired private ChampionRepository championRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private MatchGameRepository matchGameRepository;

    @Override
    public void run(String... args) {
        if (coachRepository.count() > 0) {
            return;
        }

        Map<String, Champion> champions = seedChampions();
        List<TeamRoster> rosters = seedTeamsAndPlayers(champions);
        seedMatches(rosters, champions);
    }

    private Map<String, Champion> seedChampions() {
        Champion aatrox = championRepository.save(new Champion("Aatrox", Role.TOP));
        Champion ornn = championRepository.save(new Champion("Ornn", Role.TOP));
        Champion ksante = championRepository.save(new Champion("K'Sante", Role.TOP));
        Champion jax = championRepository.save(new Champion("Jax", Role.TOP));
        Champion renekton = championRepository.save(new Champion("Renekton", Role.TOP));
        Champion gnar = championRepository.save(new Champion("Gnar", Role.TOP));

        Champion leeSin = championRepository.save(new Champion("Lee Sin", Role.JUNGLE));
        Champion vi = championRepository.save(new Champion("Vi", Role.JUNGLE));
        Champion sejuani = championRepository.save(new Champion("Sejuani", Role.JUNGLE));
        Champion viego = championRepository.save(new Champion("Viego", Role.JUNGLE));
        Champion jarvan = championRepository.save(new Champion("Jarvan IV", Role.JUNGLE));
        Champion nidalee = championRepository.save(new Champion("Nidalee", Role.JUNGLE));

        Champion orianna = championRepository.save(new Champion("Orianna", Role.MID));
        Champion ahri = championRepository.save(new Champion("Ahri", Role.MID));
        Champion azir = championRepository.save(new Champion("Azir", Role.MID));
        Champion syndra = championRepository.save(new Champion("Syndra", Role.MID));
        Champion sylas = championRepository.save(new Champion("Sylas", Role.MID));
        Champion yone = championRepository.save(new Champion("Yone", Role.MID));

        Champion jinx = championRepository.save(new Champion("Jinx", Role.ADC));
        Champion kaisa = championRepository.save(new Champion("Kai'Sa", Role.ADC));
        Champion aphelios = championRepository.save(new Champion("Aphelios", Role.ADC));
        Champion ezreal = championRepository.save(new Champion("Ezreal", Role.ADC));
        Champion xayah = championRepository.save(new Champion("Xayah", Role.ADC));
        Champion zeri = championRepository.save(new Champion("Zeri", Role.ADC));
        Champion draven = championRepository.save(new Champion("Draven", Role.ADC));
        Champion kalista = championRepository.save(new Champion("Kalista", Role.ADC));

        Champion thresh = championRepository.save(new Champion("Thresh", Role.SUPPORT));
        Champion rakan = championRepository.save(new Champion("Rakan", Role.SUPPORT));
        Champion nautilus = championRepository.save(new Champion("Nautilus", Role.SUPPORT));
        Champion lulu = championRepository.save(new Champion("Lulu", Role.SUPPORT));
        Champion leona = championRepository.save(new Champion("Leona", Role.SUPPORT));
        Champion braum = championRepository.save(new Champion("Braum", Role.SUPPORT));

        return Map.ofEntries(
                Map.entry("Aatrox", aatrox),
                Map.entry("Ornn", ornn),
                Map.entry("K'Sante", ksante),
                Map.entry("Jax", jax),
                Map.entry("Renekton", renekton),
                Map.entry("Gnar", gnar),
                Map.entry("Lee Sin", leeSin),
                Map.entry("Vi", vi),
                Map.entry("Sejuani", sejuani),
                Map.entry("Viego", viego),
                Map.entry("Jarvan IV", jarvan),
                Map.entry("Nidalee", nidalee),
                Map.entry("Orianna", orianna),
                Map.entry("Ahri", ahri),
                Map.entry("Azir", azir),
                Map.entry("Syndra", syndra),
                Map.entry("Sylas", sylas),
                Map.entry("Yone", yone),
                Map.entry("Jinx", jinx),
                Map.entry("Kai'Sa", kaisa),
                Map.entry("Aphelios", aphelios),
                Map.entry("Ezreal", ezreal),
                Map.entry("Xayah", xayah),
                Map.entry("Zeri", zeri),
                Map.entry("Draven", draven),
                Map.entry("Kalista", kalista),
                Map.entry("Thresh", thresh),
                Map.entry("Rakan", rakan),
                Map.entry("Nautilus", nautilus),
                Map.entry("Lulu", lulu),
                Map.entry("Leona", leona),
                Map.entry("Braum", braum)
        );
    }

    private List<TeamRoster> seedTeamsAndPlayers(Map<String, Champion> champions) {
        Coach kkoma = coachRepository.save(new Coach("kkOma", 12));
        Coach score = coachRepository.save(new Coach("Score", 7));
        Coach dylanFalco = coachRepository.save(new Coach("Dylan Falco", 9));
        Coach nightshare = coachRepository.save(new Coach("Nightshare", 6));
        Coach reapered = coachRepository.save(new Coach("Reapered", 10));
        Coach von = coachRepository.save(new Coach("Von", 5));

        Team t1 = saveTeam("T1", "Korea", kkoma);
        Team geng = saveTeam("Gen.G", "Korea", score);
        Team g2 = saveTeam("G2 Esports", "Europe", dylanFalco);
        Team fnatic = saveTeam("Fnatic", "Europe", nightshare);
        Team cloud9 = saveTeam("Cloud9", "North America", reapered);
        Team loud = saveTeam("LOUD", "Brazil", von);

        TeamRoster t1Roster = new TeamRoster(
                t1,
                savePlayer("Choi Woo-je", "Zeus", Role.TOP, t1, champions, "Aatrox", "Gnar", "K'Sante"),
                savePlayer("Mun Hyeon-jun", "Oner", Role.JUNGLE, t1, champions, "Lee Sin", "Viego", "Sejuani"),
                savePlayer("Lee Sang-hyeok", "Faker", Role.MID, t1, champions, "Orianna", "Azir", "Ahri"),
                savePlayer("Lee Min-hyeong", "Gumayusi", Role.ADC, t1, champions, "Jinx", "Aphelios", "Xayah"),
                savePlayer("Ryu Min-seok", "Keria", Role.SUPPORT, t1, champions, "Thresh", "Rakan", "Braum")
        );

        TeamRoster gengRoster = new TeamRoster(
                geng,
                savePlayer("Kim Ki-in", "Kiin", Role.TOP, geng, champions, "Renekton", "Jax", "K'Sante"),
                savePlayer("Kim Geon-bu", "Canyon", Role.JUNGLE, geng, champions, "Nidalee", "Lee Sin", "Viego"),
                savePlayer("Jeong Ji-hoon", "Chovy", Role.MID, geng, champions, "Azir", "Syndra", "Yone"),
                savePlayer("Kim Su-hwan", "Peyz", Role.ADC, geng, champions, "Kai'Sa", "Zeri", "Xayah"),
                savePlayer("Son Si-woo", "Lehends", Role.SUPPORT, geng, champions, "Rakan", "Nautilus", "Lulu")
        );

        TeamRoster g2Roster = new TeamRoster(
                g2,
                savePlayer("Sergen Celik", "BrokenBlade", Role.TOP, g2, champions, "K'Sante", "Jax", "Ornn"),
                savePlayer("Martin Sundelin", "Yike", Role.JUNGLE, g2, champions, "Viego", "Jarvan IV", "Vi"),
                savePlayer("Rasmus Winther", "Caps", Role.MID, g2, champions, "Orianna", "Sylas", "Syndra"),
                savePlayer("Steven Liv", "Hans sama", Role.ADC, g2, champions, "Draven", "Kalista", "Ezreal"),
                savePlayer("Mihael Mehle", "Mikyx", Role.SUPPORT, g2, champions, "Rakan", "Nautilus", "Thresh")
        );

        TeamRoster fnaticRoster = new TeamRoster(
                fnatic,
                savePlayer("Oscar Munoz", "Oscarinin", Role.TOP, fnatic, champions, "Gnar", "Renekton", "Ornn"),
                savePlayer("Ivan Martin", "Razork", Role.JUNGLE, fnatic, champions, "Vi", "Jarvan IV", "Lee Sin"),
                savePlayer("Marek Brazda", "Humanoid", Role.MID, fnatic, champions, "Azir", "Orianna", "Ahri"),
                savePlayer("Oh Hyeon-taek", "Noah", Role.ADC, fnatic, champions, "Aphelios", "Kai'Sa", "Xayah"),
                savePlayer("Yoon Se-jun", "Jun", Role.SUPPORT, fnatic, champions, "Nautilus", "Rakan", "Leona")
        );

        TeamRoster cloud9Roster = new TeamRoster(
                cloud9,
                savePlayer("Ibrahim Allami", "Fudge", Role.TOP, cloud9, champions, "Jax", "Aatrox", "Ornn"),
                savePlayer("Robert Huang", "Blaber", Role.JUNGLE, cloud9, champions, "Lee Sin", "Viego", "Nidalee"),
                savePlayer("Jang Min-soo", "EMENES", Role.MID, cloud9, champions, "Yone", "Syndra", "Sylas"),
                savePlayer("Kim Kwang-hee", "Berserker", Role.ADC, cloud9, champions, "Zeri", "Jinx", "Aphelios"),
                savePlayer("Philippe Laflamme", "Vulcan", Role.SUPPORT, cloud9, champions, "Thresh", "Leona", "Braum")
        );

        TeamRoster loudRoster = new TeamRoster(
                loud,
                savePlayer("Leonardo Souza", "Robo", Role.TOP, loud, champions, "Renekton", "Aatrox", "Gnar"),
                savePlayer("Park Jong-hoon", "Croc", Role.JUNGLE, loud, champions, "Lee Sin", "Vi", "Jarvan IV"),
                savePlayer("Thiago Sartori", "tinowns", Role.MID, loud, champions, "Orianna", "Ahri", "Syndra"),
                savePlayer("Moon Geom-su", "Route", Role.ADC, loud, champions, "Kai'Sa", "Xayah", "Ezreal"),
                savePlayer("Denilson Goncalves", "Ceos", Role.SUPPORT, loud, champions, "Rakan", "Nautilus", "Thresh")
        );

        return List.of(t1Roster, gengRoster, g2Roster, fnaticRoster, cloud9Roster, loudRoster);
    }

    private Team saveTeam(String nome, String regiao, Coach coach) {
        Team team = new Team(nome, regiao);
        team.setCoach(coach);
        return teamRepository.save(team);
    }

    private Player savePlayer(String nome, String nick, Role role, Team team, Map<String, Champion> champions, String... championNames) {
        Player player = new Player(nome, nick, role);
        player.setTeam(team);
        player.setChampions(
                List.of(championNames).stream()
                        .map(name -> findChampion(champions, name))
                        .toList()
        );
        return playerRepository.save(player);
    }

    private void seedMatches(List<TeamRoster> rosters, Map<String, Champion> champions) {
        TeamRoster t1 = rosters.get(0);
        TeamRoster geng = rosters.get(1);
        TeamRoster g2 = rosters.get(2);
        TeamRoster fnatic = rosters.get(3);
        TeamRoster cloud9 = rosters.get(4);
        TeamRoster loud = rosters.get(5);

        saveMatch("32:15", t1, g2, t1.team(), List.of(t1.mid(), t1.adc(), g2.mid(), g2.support()),
                champions, "Orianna", "Jinx", "Sylas", "Rakan");
        saveMatch("45:30", cloud9, loud, cloud9.team(), List.of(cloud9.jungle(), cloud9.adc(), loud.mid(), loud.support()),
                champions, "Lee Sin", "Zeri", "Ahri", "Nautilus");
        saveMatch("28:45", geng, fnatic, geng.team(), List.of(geng.top(), geng.mid(), fnatic.jungle(), fnatic.adc()),
                champions, "K'Sante", "Azir", "Vi", "Aphelios");
        saveMatch("38:20", g2, cloud9, g2.team(), List.of(g2.top(), g2.jungle(), cloud9.mid(), cloud9.support()),
                champions, "Jax", "Viego", "Yone", "Thresh");
        saveMatch("51:10", t1, geng, geng.team(), List.of(t1.top(), t1.support(), geng.jungle(), geng.adc()),
                champions, "Aatrox", "Braum", "Nidalee", "Kai'Sa");
        saveMatch("34:05", fnatic, loud, loud.team(), List.of(fnatic.mid(), fnatic.support(), loud.top(), loud.adc()),
                champions, "Orianna", "Leona", "Renekton", "Ezreal");
        saveMatch("29:42", t1, loud, t1.team(), List.of(t1.jungle(), t1.mid(), loud.jungle(), loud.support()),
                champions, "Lee Sin", "Azir", "Jarvan IV", "Rakan");
        saveMatch("41:18", g2, fnatic, g2.team(), List.of(g2.mid(), g2.adc(), fnatic.top(), fnatic.jungle()),
                champions, "Syndra", "Ezreal", "Gnar", "Vi");
    }

    private void saveMatch(
            String duracao,
            TeamRoster timeA,
            TeamRoster timeB,
            Team vencedor,
            List<Player> players,
            Map<String, Champion> champions,
            String... championNames
    ) {
        MatchGame match = new MatchGame(duracao);
        match.setTimeA(timeA.team());
        match.setTimeB(timeB.team());
        match.setVencedor(vencedor);
        match.setPlayers(players);
        match.setChampions(
                List.of(championNames).stream()
                        .map(name -> findChampion(champions, name))
                        .toList()
        );
        matchGameRepository.save(match);
    }

    private Champion findChampion(Map<String, Champion> champions, String name) {
        Champion champion = champions.get(name);
        if (champion == null) {
            throw new IllegalStateException("Campeao nao cadastrado na carga inicial: " + name);
        }
        return champion;
    }

    private record TeamRoster(
            Team team,
            Player top,
            Player jungle,
            Player mid,
            Player adc,
            Player support
    ) {
    }
}
