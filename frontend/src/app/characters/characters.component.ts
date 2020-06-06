import { Component, OnInit } from '@angular/core';
import { CHARACTERS} from '../mock-players';

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent implements OnInit {
  characters = CHARACTERS;

  constructor() { }

  ngOnInit(): void {
  }

}
