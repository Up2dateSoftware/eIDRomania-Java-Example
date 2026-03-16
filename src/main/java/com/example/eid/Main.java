package com.example.eid;

import com.up2date.eidromania.desktopsdk.api.EIDCard;
import com.up2date.eidromania.desktopsdk.api.EIDReadException;
import com.up2date.eidromania.desktopsdk.api.EIDReaderInfo;
import com.up2date.eidromania.desktopsdk.api.EIDRomaniaDesktopSDK;

import java.util.List;
import java.util.Scanner;

/**
 * eIDRomania Desktop SDK — Java Example Application
 *
 * Demonstrates:
 *   - SDK initialization with a license key
 *   - Listing connected PC/SC readers
 *   - Reading a Romanian eID card with CAN only (MRTD data + photo)
 *   - Reading a Romanian eID card with CAN + PIN (full data including address)
 *   - Typed error handling for every failure scenario
 *   - Progress callbacks
 *
 * Prerequisites:
 *   - A PC/SC smart card reader (contact or NFC/contactless)
 *   - A Romanian electronic identity card (CEI)
 *   - A valid eIDRomania Desktop SDK license key
 *
 * Run:
 *   GITHUB_ACTOR=<user> GITHUB_TOKEN=<token> ./gradlew run
 */
public class Main {

    // Replace with your actual license key issued by Up2Date Software SRL.
    // For testing, contact: office@up2date.ro
    private static final String LICENSE_KEY =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJpc3MiOiJSb21hbmlhbkVJRFNESyIsInN1YiI6ImNvbS5leGFtcGxlLmVpZCIsImlhdCI6MTc3MzY1OTg0MiwiZXhwIjoxNzc4ODQzODQyLCJqdGkiOiJlODI4ZGE0NS1jOGE2LTRjOTEtYjk5NS03MDFjOTVmYmI0NzkiLCJidW5kbGVJZCI6ImNvbS5leGFtcGxlLmVpZCIsImNvbXBhbnkiOiJUZXN0IiwiZmVhdHVyZXMiOlsicGFzc3BvcnRSZWFkaW5nIiwiaWRDYXJkUmVhZGluZyIsIm9jclNjYW5uaW5nIiwiY3NjYVZhbGlkYXRpb24iLCJiaW9tZXRyaWNFeHRyYWN0aW9uIiwiYWR2YW5jZWRTZWN1cml0eSJdLCJ0eXBlIjoiZGV2ZWxvcG1lbnQiLCJ2ZXJzaW9uIjoiMS4wIiwibWF4RGV2aWNlcyI6MTAwMH0" +
        ".I8PP8Tnn74wOSi2-OCSdqUl3XH0k5JFZ5dT91x0_4fRNt8I-uES7T5BwjeQYmm1oUVBwD3PNBfi999m-0ILJt2aqP1k9AGWUHp3W_o6CJdn0PwTbIcdt3SsKnXIyTh2ZjTmRx2MK57LiSUpf0iet3QzDDmpR9lDJYUkvvSq8uZ2JO-mYSkJEtPkjt0xg5SYyX7wf8V1MDWFxXtrGkdmj59htoiXlPOonzYgc9RLhCD7a25ZdK_zFj4-FC9hsUiEWI0WfJQSfioCh0iILQPz4C7PreJI09HiB-qbrLE6BmloTpvyAK8KY6gyWroCisURpHOIILf7bsneePIibOq2PAA";

    private static final String APP_IDENTIFIER = "com.example.eid";

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  eIDRomania Desktop SDK — Java Example");
        System.out.println("=================================================");
        System.out.println();

        EIDRomaniaDesktopSDK sdk = new EIDRomaniaDesktopSDK();

        try {
            // ── Step 1: Initialize ────────────────────────────────────────────
            System.out.println("[1/4] Initializing SDK...");
            sdk.initialize(LICENSE_KEY, APP_IDENTIFIER);

            if (sdk.getLicenseInfo() != null) {
                System.out.println("      License valid. Issued to: " + sdk.getLicenseInfo().getIssuedTo());
                System.out.println("      Expires: " + sdk.getLicenseInfo().getExpiresAt());
            }

            // ── Step 2: List readers ──────────────────────────────────────────
            System.out.println();
            System.out.println("[2/4] Listing PC/SC readers...");
            List<EIDReaderInfo> readers = sdk.getAvailableReaders();

            System.out.println("      Found " + readers.size() + " reader(s):");
            for (EIDReaderInfo reader : readers) {
                String cardStatus = reader.getHasCard() ? "✓ card present" : "  no card";
                System.out.printf("      [%d] %s — %s%n", reader.getIndex(), reader.getName(), cardStatus);
            }

            // Select the first reader with a card, or default to index 0
            int selectedIndex = 0;
            for (EIDReaderInfo reader : readers) {
                if (reader.getHasCard()) {
                    selectedIndex = reader.getIndex();
                    break;
                }
            }
            System.out.println("      Using reader index: " + selectedIndex);
            sdk.setActiveReader(selectedIndex);

            // ── Step 3: Read card with CAN only ───────────────────────────────
            Scanner scanner = new Scanner(System.in);
            System.out.println();
            System.out.print("[3/4] Enter CAN (6 digits from card front, or press Enter to skip): ");
            String can = scanner.nextLine().trim();

            if (!can.isEmpty()) {
                System.out.println("      Reading card with CAN only (MRTD data + face photo)...");
                System.out.println("      Keep the card on the reader until reading is complete.");
                System.out.println();

                EIDCard card = sdk.read(can);

                System.out.println();
                System.out.println("── CAN-only read result ──────────────────────────");
                printCard(card);

                // ── Step 4: Read card with CAN + PIN ─────────────────────────
                System.out.println();
                System.out.print("[4/4] Enter PIN (4 digits) for full data (or press Enter to skip): ");
                String pin = scanner.nextLine().trim();

                if (!pin.isEmpty()) {
                    System.out.println("      Reading card with CAN + PIN (full data including address)...");
                    System.out.println("      Keep the card on the reader until reading is complete.");
                    System.out.println();

                    EIDCard fullCard = sdk.read(can, pin);

                    System.out.println();
                    System.out.println("── CAN+PIN read result ───────────────────────────");
                    printCard(fullCard);
                }
            }

        } catch (Exception e) {
            if (e instanceof EIDReadException) {
                handleEIDError((EIDReadException) e);
            } else if (e instanceof IllegalArgumentException) {
                System.err.println("[ERROR] Invalid license: " + e.getMessage());
                System.err.println("        Contact Up2Date Software SRL for a valid license key.");
            } else if (e instanceof SecurityException) {
                System.err.println("[ERROR] License not authorized for this application: " + e.getMessage());
                System.err.println("        Make sure APP_IDENTIFIER matches what was used when the license was issued.");
            } else if (e instanceof IllegalStateException) {
                System.err.println("[ERROR] License expired: " + e.getMessage());
                System.err.println("        Contact Up2Date Software SRL to renew your license.");
            } else {
                System.err.println("[ERROR] Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            sdk.close();
            System.out.println();
            System.out.println("SDK closed. Goodbye.");
        }
    }

    /**
     * Handles all typed EIDReadException error codes with actionable guidance.
     */
    private static void handleEIDError(EIDReadException e) {
        System.err.println();
        System.err.println("[ERROR] Card reading failed — " + e.getErrorCode());
        System.err.println("        " + e.getMessage());
        System.err.println();

        switch (e.getErrorCode()) {
            case NOT_INITIALIZED:
                System.err.println("  → Call sdk.initialize(licenseKey, appIdentifier) before any other method.");
                break;

            case NO_READER:
                System.err.println("  → Connect a PC/SC smart card reader (contact or NFC/contactless).");
                System.err.println("  → Install the reader driver (check manufacturer website).");
                System.err.println("  → On Windows: verify the Smart Card service is running.");
                break;

            case NO_CARD:
                System.err.println("  → Place the Romanian eID card on the reader and try again.");
                break;

            case TAG_LOST:
                System.err.println("  → The card was removed or moved during reading.");
                System.err.println("  → Keep the card still on the reader for the entire ~10 seconds.");
                System.err.println("  → On Windows with ACS ACR1252: physically remove and reinsert the card.");
                break;

            case INVALID_CAN:
                System.err.println("  → The CAN (Card Access Number) is incorrect.");
                System.err.println("  → Find the 6-digit CAN on the front of the card, near the photo.");
                System.err.println("  → CAN is NOT the same as the PIN.");
                break;

            case INVALID_PIN:
                if (e.getAttemptsRemaining() > 0) {
                    System.err.println("  → The PIN is incorrect. " + e.getAttemptsRemaining() + " attempt(s) remaining.");
                    if (e.getAttemptsRemaining() == 1) {
                        System.err.println("  → WARNING: Only 1 attempt left! Do NOT retry unless you are certain.");
                        System.err.println("  → If the next attempt fails, the card will be permanently locked.");
                    }
                } else {
                    System.err.println("  → The PIN is incorrect.");
                }
                System.err.println("  → The PIN is 4 digits, set by the card owner at the DEP office.");
                break;

            case CARD_LOCKED:
                System.err.println("  → The card is permanently locked due to too many failed PIN attempts.");
                System.err.println("  → The card owner must visit a DEP (Directia Evidenta Persoanelor) office to unlock it.");
                System.err.println("  → NOTE: The card can still be read without PIN (CAN-only mode).");
                break;

            case TIMEOUT:
                System.err.println("  → Communication with the card timed out.");
                System.err.println("  → Ensure the card is firmly placed on the reader.");
                System.err.println("  → Try again, or restart the reader.");
                break;

            case UNSUPPORTED_CARD:
                System.err.println("  → This card is not a supported Romanian eID card.");
                System.err.println("  → Only Romanian electronic identity cards (CEI) are supported.");
                System.err.println("  → Romanian passports and older non-electronic IDs are not supported.");
                break;

            case READ_FAILURE:
                System.err.println("  → Failed to read card data. This may be a temporary issue.");
                System.err.println("  → Remove and reinsert the card, then try again.");
                System.err.println("  → On Windows with ACS ACR1252: this is expected on first placement.");
                System.err.println("    Remove and reinsert the card to resolve.");
                break;

            case UNKNOWN:
            default:
                System.err.println("  → Unexpected error. Check the cause for details.");
                if (e.getCause() != null) {
                    System.err.println("  → Cause: " + e.getCause().getMessage());
                }
                break;
        }
    }

    /**
     * Prints all available fields from the card in a readable format.
     */
    private static void printCard(EIDCard card) {
        System.out.println("  Personal data:");
        printField("    Surname", card.getSurname());
        printField("    Given names", card.getGivenNames());
        printField("    CNP", card.getCnp());
        printField("    Date of birth", card.getDateOfBirth());
        printField("    Sex", card.getSex());
        printField("    Nationality", card.getNationality());
        printField("    Place of birth", card.getPlaceOfBirth());

        System.out.println("  Document:");
        printField("    Document number", card.getDocumentNumber());
        printField("    Series", card.getDocumentSeries());
        printField("    Date of issue", card.getDateOfIssue());
        printField("    Date of expiry", card.getDateOfExpiry());
        printField("    Issuing authority", card.getIssuingAuthority());

        System.out.println("  Address:");
        if (card.getAddress() != null) {
            System.out.println("    " + card.getAddress());
        } else {
            System.out.println("    (not available — requires PIN)");
        }

        System.out.println("  Biometrics:");
        System.out.println("    Face photo:  " +
            (card.getFacialImageBase64() != null
                ? "available (" + card.getFacialImageBase64().length() + " chars base64)"
                : "not available (contact reader or no PIN)"));
        System.out.println("    Signature:   " +
            (card.getSignatureImageBase64() != null
                ? "available (" + card.getSignatureImageBase64().length() + " chars base64)"
                : "not available (contact reader or no PIN)"));
    }

    private static void printField(String label, String value) {
        System.out.printf("%-24s %s%n", label + ":", value != null ? value : "(not available)");
    }
}
