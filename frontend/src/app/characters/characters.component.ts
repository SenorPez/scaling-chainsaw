import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ApiService} from '../api.service';
import {Campaign, Character, EmbeddedCharacter} from '../apiobjects';

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent implements OnInit, OnChanges {
  @Input() campaign: Campaign;
  @Input() characters: EmbeddedCharacter[];
  selectedCharacter: Character;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.campaign
      && changes.campaign.previousValue
      && changes.campaign.currentValue.id !== changes.campaign.previousValue.id) {
      this.selectedCharacter = null;
    }
  }

  onClick(embeddedCharacter: EmbeddedCharacter) {
    this.apiService.getCharacter(embeddedCharacter).subscribe(character => {
      character.inventory.sort((a, b) => {
        if (a.name === 'Copper Piece') {
          return 1;
        } else if (b.name === 'Copper Piece') {
          return -1;
        }

        if (a.name === 'Silver Piece' && b.name !== 'Copper Piece') {
          return 1;
        } else if (b.name === 'Silver Piece' && a.name !== 'Copper Piece') {
          return -1;
        }

        if (a.name === 'Electrum Piece' &&
          !(b.name === 'Silver Piece' || b.name === 'Copper Piece')) {
          return 1;
        } else if (b.name === 'Electrum Piece' &&
          !(a.name === 'Silver Piece' || a.name === 'Copper Piece')) {
          return -1;
        }

        if (a.name === 'Gold Piece' &&
          !(b.name === 'Electrum Piece' || b.name === 'Silver Piece' || b.name === 'Copper Piece')) {
          return 1;
        } else if (b.name === 'Gold Piece' &&
          !(a.name === 'Electrum Piece' || a.name === 'Silver Piece' || a.name === 'Copper Piece')) {
          return -1;
        }

        if (a.name === 'Platinium Piece' &&
          !(b.name === 'Gold Piece' || b.name === 'Electrum Piece' || b.name === 'Silver Piece' || b.name === 'Copper Piece')) {
          return 1;
        } else if (b.name === 'Platinum Piece' &&
          !(a.name === 'Gold Piece' || a.name === 'Electrum Piece' || a.name === 'Silver Piece' || a.name === 'Copper Piece')) {
          return -1;
        }

        return a.name > b.name ? 1 : b.name > a.name ? -1 : 0;
      });
      this.selectedCharacter = character;
    });
  }
}
