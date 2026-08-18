# Play Console — inzendgegevens voor GeoGrocery (interne test)

Alle teksten hieronder kun je rechtstreeks kopiëren naar de betreffende Play Console-formulieren.

## App-gegevens
- **App-naam:** GeoGrocery
- **Pakketnaam:** `com.conspect.geogrocery` (wordt vastgezet bij eerste upload)
- **Standaardtaal:** Nederlands (nl-NL)
- **Type / prijs:** App, gratis
- **Categorie:** Productiviteit (of Winkelen)

## Korte beschrijving (max 80 tekens)
Boodschappenlijstjes met een herinnering zodra je bij de winkel aankomt.

## Volledige beschrijving
GeoGrocery koppelt je boodschappenlijstjes aan een winkellocatie. Zodra je fysiek in de buurt
van die winkel komt, krijg je automatisch een melding met je openstaande boodschappen — zo vergeet
je nooit meer iets. Zoek winkels via OpenStreetMap, stel per lijst een straal in, en vink items af.
Geen account nodig, geen advertenties; je lijstjes blijven op je eigen toestel.

## Location Permissions Declaration (achtergrondlocatie)
Play vraagt waarom de app `ACCESS_BACKGROUND_LOCATION` nodig heeft. Gebruik deze tekst:

> GeoGrocery gebruikt achtergrondlocatie uitsluitend voor geofencing: de app stuurt een lokale
> melding met de boodschappenlijst zodra de gebruiker aankomt bij een winkel die hij zelf aan een
> lijst heeft gekoppeld. Dit is een kernfunctie van de app en werkt ook wanneer de app gesloten is.
> De locatie wordt volledig op het toestel verwerkt via de Google Geofencing API; er wordt geen
> locatiegegeven verzonden, gedeeld of opgeslagen.

- **Kernfunctie?** Ja.
- **Werkt de functie zonder achtergrondlocatie?** Nee — zonder achtergrondlocatie kan de melding
  niet afgaan als de app dicht is.

## Data Safety (gegevensbeveiliging)
- **Verzamelt de app gegevens?** Nee (er wordt niets naar de ontwikkelaar of servers gestuurd).
- **Deelt de app gegevens?** Alleen de door de gebruiker getypte zoektekst wordt naar
  OpenStreetMap Nominatim gestuurd om zoekresultaten te tonen. Geen identifiers.
- **Locatie:** wordt op het toestel gebruikt voor geofencing, niet verzameld/verzonden.
- **Versleuteling in transit:** ja (HTTPS naar Nominatim).
- **Gegevens verwijderen:** lokaal; via app-gegevens wissen of de-installeren.

## Privacybeleid-URL
Vereist. Zie `docs/PRIVACY.md`. Host deze op een publieke URL (bv. GitHub Pages, of vraag mij om
'm als webpagina te publiceren) en plak de URL in Play Console → App-content → Privacybeleid.

## Content rating
Vragenlijst invullen: geen geweld/seks/gokken/gebruikersinteractie → verwachte rating: Iedereen.

## Doelgroep
Doelgroep 18+ of 13+ (naar keuze); app is niet voor kinderen bedoeld.

## Interne test — stappen
1. Play Console → **Testen → Interne tests → Nieuwe release maken**.
2. Upload `GeoGrocery-v1-release.aab` (van de GitHub-release `play-1`).
3. Accepteer **Play App Signing** (aanbevolen).
4. Vul release-notitie in (bv. "Eerste interne testversie").
5. **Testers**-tabblad → maak een e-maillijst met je eigen Google-account → opslaan.
6. **Review release → Uitrollen naar interne test**.
7. Kopieer de **opt-in-link**, open die op je telefoon (zelfde Google-account), word tester en
   installeer via Play.

## App Actions / "Hey Google"
Zodra de app via Play (welke track dan ook) op je toestel is geïnstalleerd, is de al aanwezige
`CREATE_ITEM_LIST`-capability actief. Test: "Hey Google, maak een boodschappenlijst voor
Dekamarkt Zeewolde". (Geef Google Assistant na installatie soms een dag om de app te indexeren.)

## Belangrijk
- **versionCode** moet bij elke nieuwe upload omhoog. Nu `1`; ik verhoog 'm bij volgende releases.
- Bewaar de upload-sleutel (`app/upload.keystore`, wachtwoord `geogrocery`, alias `upload`). Met
  Play App Signing is verlies herstelbaar via Google, maar houd 'm veilig.
