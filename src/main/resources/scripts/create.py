import json

default_seasons = ["SUMMER", "AUTUMN", "WINTER", "SPRING"]
default_type = ["satisfaction", "supporter", "resources"]
default_resources = ["INDUSTRY", "FARMING", "TREASURY", "FOODUNIT"]


def satisfaction_effect(effect):
    effect["faction"] = input("faction : ")
    effect["value"] = int(input("value : "))

def supporter_effect(effect):
    satisfaction_effect(effect)
    effect["percentage"] = int(input("percentage : [1/0]")) == 1

def resources_effect(effect):
    while True:
        resource = input("resource : {}".format(default_resources))

        if resource in default_resources:
            break
        print("resource incorrect")

        

    effect["resource"] = resource
    effect["value"] = int(input("value : "))

type_effect = {
    "satisfaction": satisfaction_effect,
    "supporter": supporter_effect,
    "resources": resources_effect
}

def write_scenario(filename, scenario):
    with open("./../src/main/resources/scenario/{}.json".format(filename), "w") as f:
        json.dump(scenario, f)

def create(creator, name):
    collection = []

    while True:
        collection.append(creator())

        if input("Ajouter un autre {} ? [O/n] ".format(name)) not in {"o", "O", ""}:
            return collection

def create_event():
    print("\t Event: ")
    event = {
        "name": input("Name : ")
    }

    seasons = create_seasons()
    if seasons:
        event["seasons"] = seasons

    if input("Ajouter des choix ? [O/n] ") in {"o", "O", ""}:
        event["choices"] = create(create_choice, "choix")

    return event

def create_seasons():
    seasons = input("Seasons : {}".format(default_seasons)).upper().split()
    return [season for season in seasons if season in default_seasons]

def create_choice():
    print("\t Choix: ")
    choice = {
        "label": input("label : ")
    }

    if input("Ajouter des effects ? [O/n] ") in {"o", "O", ""}:
        choice["effects"] = create(create_effect, "effect")

    if input("next ? [O/n] ") in {"o", "O", ""}:
        choice["next"] = create_event()
    return choice

def create_effect():
    print("\t Effect: ")

    effect = {}

    while True:
        type_ = input("type : {}".format(default_type))

        if type_  in default_type:
            break
        print("type incorrect")
        
    
    effect["type"] = type_

    type_effect[type_](effect)
    return effect


if __name__ == '__main__':
    filename = input("filename: ")
    scenario = create(create_event, "event")
    write_scenario(filename, scenario)