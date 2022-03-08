# JELU

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/bayang/jelu/CI?style=flat-square) ![GitHub](https://img.shields.io/github/license/bayang/jelu?style=flat-square) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/bayang/jelu?style=flat-square) ![Docker Image Version (tag latest semver)](https://img.shields.io/docker/v/wabayang/jelu/latest?label=docker%20hub&style=flat-square)


## Purpose

This app main purpose is to track read books and to-read list.

It acts as a self hosted "personal Goodreads" because I became tired of switching providers every time an online service was shut.

I became tired of having to export and reimport my data each time with data loss in the process.

Also I wanted control on my data, so Jelu offers an API you can script or integrate with third party tools or services (which you cannot do with the vast majority of other online services).

All my data is now located into a single-file database which can be saved anywhere.

## Features

* track read books so you don't have to remember everything
* manage to-read list
* Import history (from goodreads for now, via csv export)
* Import single books manually or automatically via online search (through title, authors or isbn)
* Mark books as currently reading, finished or dropped
* Books can be tagged and a tag page can display all books with that tag
* Links to third party providers are fetched online (google books, amazon, goodreads, librarythings) or computed from those providers id you could enter manually.

## Usage

* Import your existing history if you have a Goodreads account
* Start recording your read books
* Add books you want to read
* Edit tags, books, import and change covers (either from a file on disk or from a url) ...

## Installation

### Java

* download the java Jar from the releases section in a dedicated folder
* go to this folder
* start the jar (it is a spring fat jar so dependencies are included) : eg `java -jar jelu-0.5.0.jar`
* If you want to tweak the default config (see `src/main/resources/application.yml`), just create a yaml file called application.yml in the same foler as the jar.
 
For example if you want the database to be located next to the jar file (instead of being located in the default `${user.home}/.jelu/database/` folder) :

```yaml
jelu:
  database:
    path: .
```

The automatic metadata online search is provided for the moment through a calibre tool called fetch-ebook-metadata (whether you like it or not).

So if you want to use it with the java install, provide the path to the executable in the config, like so : 

```yaml
jelu:
  metadata:
    calibre:
      path: /usr/bin/fetch-ebook-metadata
```

If you run into a cors issue, update the config with the desired origins like so : 

```yaml
jelu:
  cors.allowed-origins:
    - http://localhost:3000
```

Then open the web UI in your web browser at `localhost:11111`

### Docker

An image is available here : 

https://hub.docker.com/repository/docker/wabayang/jelu

This one is the easiest if you are used to it.

The docker image we provide embeds the fetch-ebook-metada executable to automatically import books based on their title, authors or isbn.

A sample docker compose would look like that : 

```dockerfile
version: '3.3'
services:
  jelu:
    image: wabayang/jelu
    container_name: jelu
    volumes:
      - type: bind
        source: ~/jelu/config
        target: /config
      - type: bind
        source: ~/jelu/database
        target: /database
      - type: bind
        source: ~/jelu/files
        target: /files
      - type: bind
        source: /etc/timezone
        target: /etc/timezone
        read_only: true
    ports:
      - 11111:11111
    user: "1000:1000"
    environment:
      - MYENV=test
    restart: unless-stopped

```

!!!! WARNING : ARM versions are built but have not been tested yet !

## Screenshots

Home page : 

![home page](screenshots/home-page.png)

Auto import form (empty) : 

![](screenshots/auto-import-empty.png)

Auto import form (filled) :

![](screenshots/auto-import-filled.png)

Auto import form (result preview) :

![](screenshots/auto-import-preview-result.png)

Auto import form (edit pre-filled results before importing to your account, eg : modify tags etc...) :

![](screenshots/auto-import-edit-result.png)

Books list : 

![](screenshots/book-list.png)

Book detail page :

![](screenshots/book-detail-1.png)

Book detail, events part :

![](screenshots/book-detail-events.png)
