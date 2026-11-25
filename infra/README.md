# OpenTofu: erste EC2-Instanz für TicTacTest

## Ziel

Erste Infrastruktur-Konfiguration im TicTacTest-Repository: Eine EC2-Instanz (`t2.micro`) im AWS Learner Lab, erreichbar per SSH mit einem eigenen Schlüsselpaar. Das Setup bereitet das spätere Deployment von TicTacTest als Online-Container vor: Die Instanz ist der Rechner, auf dem später ein Container laufen kann.

## Zugang

Der Zugang zum AWS Learner Lab funktioniert. Die temporären Zugangsdaten aus dem Lab (Access Key, Secret Key, Session Token) stehen in `~/.aws/credentials`, Region `us-east-1`. Bestätigt mit `aws sts get-caller-identity`. Die Zugangsdaten laufen nach jeder Session ab und müssen neu kopiert werden.

## Begriffe

- **Infrastructure as Code (IaC):** Infrastruktur wird in Textdateien beschrieben statt von Hand angeklickt. Die Dateien lassen sich versionieren, prüfen und beliebig oft wieder ausführen.
- **OpenTofu / Terraform:** Werkzeuge, die diese Dateien lesen und die beschriebene Infrastruktur herstellen. OpenTofu ist der Open-Source-Fork von Terraform. Dateien (`.tf`) und Konzepte sind gleich, der Befehl heisst `tofu` statt `terraform`.
- **Provider:** Das Plugin für eine Plattform. Hier `aws` (`provider "aws"` in `main.tf`).
- **Resource:** Ein Objekt, das erstellt wird. Hier `aws_key_pair`, `aws_security_group` und `aws_instance`.
- **Variable:** Ein Eingabewert, der die Konfiguration anpassbar macht. Hier `region`, `instance_type`, `public_key_path` und `ssh_allowed_cidr`.
- **Output:** Ein Wert, der nach dem Erstellen angezeigt wird. Hier `public_ip`, `public_dns` und `ssh_command`.
- **State:** Die Datei, in der OpenTofu festhält, welche reale Ressource zu welchem Block der Konfiguration gehört.

## Aufbau der Konfiguration

Dateien im Ordner `infra/`: `main.tf`, `variables.tf`, `outputs.tf` und `terraform.tfvars.example`.

- **Provider:** Verbindet OpenTofu mit AWS. Die Region kommt aus der Variable `region`. Die Provider-Version ist mit `~> 5.0` eingeschränkt und in `.terraform.lock.hcl` festgehalten.
- **AMI:** Eine `data`-Quelle sucht die aktuelle Amazon-Linux-2023-Vorlage, statt eine ID fest einzutragen.
- **Key-Pair:** Enthält nur den öffentlichen Schlüssel. Der private Schlüssel liegt ausserhalb des Repositorys in `~/.ssh`.
- **Security Group:** Erlaubt SSH (Port 22) nur von der eigenen IP-Adresse (`/32`) und nicht von `0.0.0.0/0`. Offene SSH-Ports werden im Internet sehr schnell gescannt. Nachteil: Wechselt die IP, muss `terraform.tfvars` angepasst und `tofu apply` erneut ausgeführt werden.
- **Instance:** `t2.micro`, verwendet Key-Pair und Security Group.

## Nutzung

1. SSH-Schlüssel erstellen: `ssh-keygen -t ed25519 -f ~/.ssh/tictactest-aws`
2. `infra/terraform.tfvars.example` nach `infra/terraform.tfvars` kopieren und die eigene IP-Adresse als `x.x.x.x/32` eintragen.
3. Aktuelle Zugangsdaten aus dem Learner Lab in `~/.aws/credentials` eintragen.
4. Im Repository-Ordner: `tofu -chdir=infra init`, dann `plan` und `apply`.
5. Verbinden mit dem Output `ssh_command`.
6. Aufräumen: `tofu -chdir=infra destroy`.

## Ablauf

`tofu init` → `tofu validate` → `tofu plan` → `tofu apply` → SSH-Test → `tofu destroy`

- `init` lädt den Provider.
- `validate` prüft die Konfiguration.
- `plan` vergleicht Konfiguration, State und Realität und zeigt die Änderungen, ohne sie auszuführen. Hier: 3 Ressourcen werden hinzugefügt.
- `apply` erstellt die Ressourcen.
- `destroy` entfernt alles wieder.

SSH-Test mit dem eigenen Schlüssel: `whoami` ergab `ec2-user`, `hostname` ergab `ip-172-31-25-143.ec2.internal`. Danach wurde die Umgebung mit `tofu destroy` entfernt.

## State und Git

**Warum der State wichtig ist:** Ohne State wüsste OpenTofu nicht, welche Instanz zu seiner Konfiguration gehört. Es würde Ressourcen doppelt erstellen oder bestehende nicht löschen. Der State kann sensible Werte enthalten.

**In Git gehören:** die `.tf`-Dateien, `terraform.tfvars.example`, `.terraform.lock.hcl` (fixiert die Provider-Version) und `.gitignore`.

**Nicht in Git gehören:**
- `terraform.tfvars` (lokale Werte, enthält die eigene IP)
- `*.tfstate` (kann sensible Informationen enthalten)
- `.terraform/` (heruntergeladene Provider)
- private SSH-Schlüssel (liegen in `~/.ssh`)
- AWS-Zugangsdaten (liegen in `~/.aws/credentials`)

Das ist in der `.gitignore` umgesetzt. In produktiven Umgebungen liegt der State in einem geschützten Remote-Backend.

## Kostenkontrolle

Eine laufende Instanz kostet pro Stunde, auch wenn niemand sie benutzt. Massnahmen:
1. `tofu destroy` direkt nach dem Test.
2. Die kleinste sinnvolle Instanz (`t2.micro`).
3. Das Budget im Learner Lab regelmässig kontrollieren.

IaC hilft bei der Kostenkontrolle, weil die ganze Umgebung mit einem Befehl vollständig entfernt wird, ohne dass etwas vergessen geht. Sie lässt sich ebenso schnell wieder aufbauen. Die Konfiguration zeigt zudem jederzeit, was existiert.

## Ausblick: CI/CD und dev, staging, prod

Eine Pipeline kann bei jeder Änderung `tofu validate` und `tofu plan` ausführen und den Plan zur Prüfung anzeigen. `tofu apply` läuft erst nach Freigabe. Für dev, staging und prod gibt es je eigene Variablen und einen eigenen State. So entstehen gleiche, nachvollziehbare Umgebungen, und Infrastrukturänderungen durchlaufen denselben Review-Prozess wie Code. Für TicTacTest folgt später ein Container-Image aus der GitHub Container Registry, das auf dieser Infrastruktur läuft.
