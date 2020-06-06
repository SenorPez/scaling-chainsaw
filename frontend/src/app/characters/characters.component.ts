import {Component, OnInit} from '@angular/core';
import {CharactersService} from '../characters.service';
import {Character, Characters} from '../characters';

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent implements OnInit {
  characters: Characters[];
  selectedCharacter: Character;

  constructor(private characterService: CharactersService) { }

  ngOnInit(): void {
    this.getCharacters();
  }

  getCharacters(): void {
    this.characterService.getCharacters().subscribe(observable => {
      this.characters = observable._embedded['loot-api:character'];
    });
  }

  onClick(character: Characters) {
    this.characterService.getCharacter(character.id).subscribe(observable => {
      this.selectedCharacter = observable;

    });
  }
}
