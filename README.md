<h1 align="center">
  <br>
  Assocify
  <br>
</h1>

Assocify is an application to centralize all of your student organization needs in one place: accounting, chat, event management, todo list, annoucements, minutes of meeting...

## Key Features

- Chat & Annoucements
- Budgets & Transactions
- Receipt Management
- Security & Authentication
- Offline Access
- Audio Notes
- Dynamic To-Do List
- Event Map
- Personalized Schedule
- Gamification of Staffing
- When to Meet

## Team
| Name                |                                     GitHub username  |
|---------------------|------------------------------------------------------|
| Lorenzo Padrini     | [ortoLover](https://github.com/ortoLover)            |
| Sarah Badr          | [sarahbadr17](https://github.com/sarahbadr17)        |
| Maï-Linh Cordonnier | [Mai-LinhC](https://github.com/Mai-LinhC)            |
| Rayan Boucheny      | [Polymeth](https://github.com/polymeth)              |
| François Théron     | [ZiZouet](https://github.com/ZiZouet)                |
| Sebastien Kobler    | [SekoiaTree](https://github.com/SekoiaTree)          |
| Sidonie Bouthors    | [SidonieBouthors](https://github.com/SidonieBouthors)|

## Quick Links

[Figma](https://www.figma.com/files/project/213391728/Assocify?fuid=1213058493509425919)
[Project Proposal](https://docs.google.com/document/d/1_9hGwoGBIqygBJgahw5CbMNxU-d_K-2d-qFGFdvNsdA/edit)

## Setup Dev environment

This repository uses client hooks to ensure the quality of the commits. You need to install the `pre-commit` python package to set up those:

```sh
# Using apt
apt-get update
apt-get install pip
pip install pre-commit

# Using pacman
pacman -S python-pre-commit
```

Then run:

```sh
pre-commit install --hook-type commit-msg
```

All the commits must follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification. It is checked both locally by the hook and by the CI on pull requests.
